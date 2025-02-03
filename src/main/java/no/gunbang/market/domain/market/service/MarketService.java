package no.gunbang.market.domain.market.service;

import java.time.LocalDateTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<MarketPopularResponseDto> getPopulars(Pageable pageable) {
        return marketRepository.findPopularMarketItems(
            START_DATE,
            pageable
        );
    }

    public Page<MarketListResponseDto> getAllMarkets(
        Pageable pageable,
        String searchKeyword,
        String sortBy,
        String sortDirection
    ) {
        return marketRepository.findAllMarketItems(
            searchKeyword,
            sortBy,
            sortDirection,
            pageable
        );
    }

    public List<MarketResponseDto> getSameItems(Long itemId) {
        return marketRepository.findByItemIdOrderByPriceAsc(itemId)
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
    public MarketTradeResponseDto tradeMarket(
        Long userId,
        MarketTradeRequestDto requestDto
    ) {
        Item foundItem = findItemById(requestDto.getItemId());

        Long marketId = requestDto.getMarketId();

        Market foundMarket = findMarketById(marketId);

        int buyAmount = requestDto.getAmount();
        int sellAmount = foundMarket.getAmount();

        if (buyAmount > sellAmount) {
            log.info("재고가 구매량보다 부족하여 현재 재고 전부 구매합니다 : {}", sellAmount);
            buyAmount = sellAmount;
        }

        User buyer = findUserById(userId);

        long price = foundMarket.getPrice();

        // 구매자는 인벤에 아이템 증가 판매자/마켓은 감소

        // 람다 내부 에서의 변수 사용을 위해 final 변수로 할당
        final int finalBuyAmount = buyAmount;
        final long finalPrice = price;

        // 마켓의 아이템 수 차감시 락
        lockStrategy.execute(Market.class, foundMarket.getId().toString(), 1000L, 3000L, () -> {
            foundMarket.decreaseAmount(finalBuyAmount);
            return null;
        });
        inventoryService.updateInventory(foundItem, buyer, buyAmount);

        // 구매자의 골드 차감 시 락
        lockStrategy.execute(User.class, buyer.getId().toString(), 1000L, 3000L, () -> {
            buyer.decreaseGold(finalBuyAmount * finalPrice);
            return null;
        });

        Trade tradeToSave = Trade.of(
            buyer,
            foundMarket,
            buyAmount,
            buyAmount * price
        );

        Trade savedTrade = tradeRepository.save(tradeToSave);

        return MarketTradeResponseDto.toDto(savedTrade);
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
