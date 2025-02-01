package no.gunbang.market.domain.market.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.Inventory;
import no.gunbang.market.common.InventoryRepository;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.common.Status;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketRegisterRequestDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.dto.MarketTradeRequestDto;
import no.gunbang.market.domain.market.dto.MarketTradeResponseDto;
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

    private final MarketRepository marketRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final TradeRepository tradeRepository;
    private final ItemRepository itemRepository;
    private final InventoryService inventoryService;

    public Page<MarketListResponseDto> getPopulars(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return marketRepository.findPopularMarketItems(startDate, pageable);
    }

    public Page<MarketListResponseDto> getAllMarkets(Pageable pageable) {
        return marketRepository.findAllMarketItems(pageable);
    }

    @Transactional
    public MarketResponseDto registerMarket(
        Long userId,
        MarketRegisterRequestDto registerRequestDto) {

        User user = findUserById(userId);

        int amount = registerRequestDto.getAmount();
        Long itemId = registerRequestDto.getItemId();
        Item item = findItemById(itemId);

        // 인벤토리에 그만큼 개수 갖고 있는지 검사
        Inventory inventory = findInventoryByUserIdAndItemId(userId, itemId);
        inventory.validateAmount(amount);

        Market market = Market.of(
            registerRequestDto.getAmount(),
            registerRequestDto.getPrice(),
            Status.ON_SALE,
            user,
            item
        );

        marketRepository.save(market);

        return MarketResponseDto.toDto(market);
    }

    @Transactional
    public MarketTradeResponseDto tradeMarket(Long userId, MarketTradeRequestDto tradeRequestDto) {

        Item item = findItemById(tradeRequestDto.getItemId());

        Long marketId = tradeRequestDto.getMarketId();
        Market market = findMarketById(marketId);

        int buyAmount = tradeRequestDto.getAmount();
        int sellAmount = market.getAmount();

        if (buyAmount > sellAmount) {
            log.info("재고가 구매량보다 부족하여 현재 재고 전부 구매합니다 : {}", sellAmount);
            buyAmount = sellAmount;
        }

        User buyer = findUserById(userId);
        long price = market.getPrice();

        if (buyer.getGold() < buyAmount * price) {
            throw new CustomException(ErrorCode.LACK_OF_GOLD);
        }

        User seller = market.getUser();

        // 구매자는 인벤에 아이템 증가 판매자/마켓은 감소
        market.decreaseAmount(buyAmount);
        inventoryService.updateInventory(item, seller, buyAmount*-1);
        inventoryService.updateInventory(item, buyer, buyAmount);

        Trade trade = Trade.of(buyer, market, buyAmount, buyAmount * price);
        tradeRepository.save(trade);
        return MarketTradeResponseDto.toDto(trade);
    }

    public void deleteMarket(Long userId, Long marketId) {

        Market market = findMarketById(marketId);
        market.validateUser(userId);

        market.delete();

    }

    /*
    여기서 부터 헬퍼 클래스
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
