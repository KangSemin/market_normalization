package no.gunbang.market.domain.market.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.Inventory;
import no.gunbang.market.common.InventoryRepository;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.common.Status;
import no.gunbang.market.common.aspect.SemaphoreLock;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.market.cursor.MarketCursorValues;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketPopularResponseDto;
import no.gunbang.market.domain.market.dto.MarketRegistrationRequestDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.dto.MarketTradeRequestDto;
import no.gunbang.market.domain.market.dto.MarketTradeResponseDto;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.market.repository.MarketRepository;
import no.gunbang.market.domain.market.repository.TradeRepository;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MarketService {

    private static final LocalDateTime START_DATE = LocalDateTime.now().minusDays(30);

    private final MarketRepository marketRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final TradeRepository tradeRepository;
    private final ItemRepository itemRepository;

    public List<MarketPopularResponseDto> getPopulars(Long lastTradeCount, Long lastItemId) {
        return marketRepository.findPopularMarketItems(
            START_DATE,
            lastTradeCount,
            lastItemId
        );
    }

    public List<MarketListResponseDto> getAllMarkets(
        String searchKeyword,
        String sortBy,
        String sortDirection,
        Long lastItemId,
        MarketCursorValues values
    ) {
        return marketRepository.findAllMarketItems(
            searchKeyword,
            sortBy,
            sortDirection,
            lastItemId,
            values
        );
    }

    public List<MarketResponseDto> getSameItems(Long itemId) {
        return marketRepository.findByItemIdOrderByPriceAscCreatedAtAsc(itemId)
            .stream()
            .map(MarketResponseDto::toDto)
            .toList();
    }

    @Transactional
    public MarketResponseDto registerMarket(
        Long userId,
        MarketRegistrationRequestDto requestDto
    ) {

        User foundUser = findUserById(userId);

        Long itemId = requestDto.getItemId();

        Item foundItem = findItemById(itemId);

        int amount = requestDto.getAmount();

        // 인벤토리에 그만큼 개수 갖고 있는지 검사
        Inventory inventory = findInventoryByUserIdAndItemId(
            userId,
            itemId
        );

        inventory.validateAmount(amount);

        inventory.updateInventory( amount * -1);

        Market marketToRegister = Market.of(
            amount,
            requestDto.getPrice(),
            Status.ON_SALE,
            foundUser,
            foundItem
        );

        Market registeredMarket = marketRepository.save(marketToRegister);

        return MarketResponseDto.toDto(registeredMarket);
    }

    @SemaphoreLock(key = "trade_market", maxUsers = 100, expireTime = 5000)
    @Transactional
    public List<MarketTradeResponseDto> tradeMarket(
        Long buyerId,
        MarketTradeRequestDto requestDto
    ) {
        Long itemId = requestDto.getItemId();

        Item foundItem = findItemById(itemId);

        List<Market> availableMarkets = marketRepository.findByItemIdOrderByPriceAscCreatedAtAsc(itemId);
        if (availableMarkets.isEmpty()) {
            throw new CustomException(ErrorCode.AVAILABLE_MARKET_NOT_FOUND);
        }

        int remainedAmountToBuy = requestDto.getAmount();
        User buyer = userRepository.findByIdForUpdate(buyerId);


        List<MarketTradeResponseDto> tradeResponses = new ArrayList<>();

        for (Market market : availableMarkets) {
            if (remainedAmountToBuy == 0) break;

            // 비관적 락을 위해 해당 마켓 엔티티를 재조회
            Market lockedMarket = marketRepository.findByIdForUpdate(market.getId());

            int marketAvailableAmount = lockedMarket.getAmount();
            int purchasedAmount = Math.min(remainedAmountToBuy, marketAvailableAmount);
            long price = lockedMarket.getPrice();
            long totalCost = purchasedAmount * price;

            // 판매자도 비관적 락을 적용하여 조회
            User seller = userRepository.findByIdForUpdate(lockedMarket.getUser().getId());

            // 재고 차감
            lockedMarket.decreaseAmount(purchasedAmount);
            // 구매자 인벤토리 업데이트
            updateOrCreateInventory(foundItem, buyer, purchasedAmount);

            // 구매자 골드 차감
            buyer.decreaseGold(totalCost);
            // 판매자 골드 증가
            seller.increaseGold(totalCost);

            Trade trade = Trade.of(buyer, lockedMarket, purchasedAmount, totalCost);
            Trade savedTrade = tradeRepository.save(trade);
            tradeResponses.add(MarketTradeResponseDto.toDto(savedTrade));

            remainedAmountToBuy -= purchasedAmount;
        }

        if (remainedAmountToBuy > 0) {
            log.info("구매 요청 일부 실패. 남은 수량: {}", remainedAmountToBuy);
        }

        return tradeResponses;
    }

    @Transactional
    public void deleteMarket(Long userId, Long marketId) {


        Market foundMarket = findMarketById(marketId);

        foundMarket.validateUser(userId);

        Inventory foundInventory = findInventoryByUserIdAndItemId(userId, foundMarket.getItem().getId());

        foundInventory.updateInventory(foundMarket.getAmount());

        foundMarket.delete();
    }

    private void updateOrCreateInventory(Item item, User user, int amount) {
        Inventory inventory = inventoryRepository
            .findByUserIdAndItemIdForUpdate(user.getId(), item.getId())
            .orElse(null);

        if (inventory == null) {
            if (amount < 0) {
                throw new CustomException(ErrorCode.LACK_OF_SELLER_INVENTORY);
            }
            inventory = new Inventory(item, user, amount);
            inventoryRepository.save(inventory);
        } else {
            inventory.updateInventory(amount);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
    }

    private Market findMarketById(Long marketId) {
        return marketRepository.findById(marketId)
            .orElseThrow(() -> new CustomException(ErrorCode.MARKET_NOT_FOUND));
    }

    private Inventory findInventoryByUserIdAndItemId(Long userId, Long itemId) {
        return inventoryRepository.findByUserIdAndItemId(userId, itemId)
            .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
    }

}
