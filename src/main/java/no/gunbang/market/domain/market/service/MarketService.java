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
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.common.lock.LockStrategy;
import no.gunbang.market.domain.market.dto.*;
import no.gunbang.market.domain.market.dto.MarketPopularResponseDto;
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
    private final InventoryService inventoryService;
    private final LockStrategy lockStrategy;

    public List<MarketPopularResponseDto> getPopulars(Long lastMarketId) {
        return marketRepository.findPopularMarketItems(
            START_DATE,
            lastMarketId
        );
    }

    public List<MarketListResponseDto> getAllMarkets(
        Long lastMarketId,
        String searchKeyword,
        String sortBy,
        String sortDirection
    ) {
        return marketRepository.findAllMarketItems(
            searchKeyword,
            sortBy,
            sortDirection,
            lastMarketId
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

        inventoryService.updateInventory(foundItem, foundUser, amount * -1);

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

    @Transactional
    public List<MarketTradeResponseDto> tradeMarket(
        Long buyerId,
        MarketTradeRequestDto requestDto
    ) {
        Long itemId = requestDto.getItemId();
        Item foundItem = findItemById(requestDto.getItemId());

        List<Market> availableMarkets = marketRepository
            .findByItemIdOrderByPriceAscCreatedAtAsc(itemId);

        if (availableMarkets.isEmpty()) {
            throw new CustomException(ErrorCode.AVAILABLE_MARKET_NOT_FOUND);
        }

        int remainedAmountToBuy = requestDto.getAmount();
        User buyer = findUserById(buyerId);

        return processMarketTrades(buyer, foundItem, remainedAmountToBuy, availableMarkets);
    }

    @Transactional
    public void deleteMarket(Long userId, Long marketId) {

        User foundUser = findUserById(userId);

        Market foundMarket = findMarketById(marketId);

        foundMarket.validateUser(userId);

        inventoryService.updateInventory(foundMarket.getItem(), foundUser, foundMarket.getAmount());

        foundMarket.delete();

    }

    /*
    여기서 부터 헬퍼 메서드
     */

    private List<MarketTradeResponseDto> processMarketTrades(User buyer, Item foundItem, int remainedAmountToBuy, List<Market> availableMarkets) {
        List<MarketTradeResponseDto> tradeResponses = new ArrayList<>();

        // 구매 가능한 마켓을 차례대로 탐색 후 로직
        for (Market market : availableMarkets) {

            if (remainedAmountToBuy == 0) break;

            int marketAvailableAmount = market.getAmount();

            // 구매량이 많으면 재고 수 만큼 구매
            int purchasedAmount = Math.min(remainedAmountToBuy, marketAvailableAmount);

            long price = market.getPrice();
            long totalCost = purchasedAmount * price;

            User seller = market.getUser();

            lockStrategy.execute(
                Market.class,
                market.getClass().getSimpleName() + ":" + market.getId(),
                1000L,
                3000L,
                () -> {
                market.decreaseAmount(purchasedAmount);
                return null;
            });

            // 구매자 인벤토리 아이템 증가
            inventoryService.updateInventory(foundItem, buyer, purchasedAmount);

            // 구매자의 골드 차감 락
            lockStrategy.execute(
                User.class,
                buyer.getClass().getSimpleName() + ":" + buyer.getId(),
                1000L,
                3000L,
                () -> {
                buyer.decreaseGold(totalCost);
                return null;
            });

            // 판매자의 골드 증가 락
            lockStrategy.execute(
                User.class,
                seller.getClass().getSimpleName() + ":" + seller.getId(),
                1000L,
                3000L,
                () -> {
                seller.increaseGold(totalCost);
                return null;
            });

            Trade trade = Trade.of(buyer, market, purchasedAmount, totalCost);
            Trade savedTrade = tradeRepository.save(trade);
            tradeResponses.add(MarketTradeResponseDto.toDto(savedTrade));

            // 남은 구매량 감소
            remainedAmountToBuy -= purchasedAmount;
        }

        if (remainedAmountToBuy > 0) {
            log.info("구매 요청 일부 실패 남은 수량: {}", remainedAmountToBuy);
        }

        return tradeResponses;
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
