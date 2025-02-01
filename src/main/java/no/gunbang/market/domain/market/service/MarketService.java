package no.gunbang.market.domain.market.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.Inventory;
import no.gunbang.market.common.InventoryRepository;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketRegisterRequestDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.dto.MarketTradeRequestDto;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.repository.MarketRepository;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;


    public Page<MarketListResponseDto> getPopulars(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return marketRepository.findPopularTradeItems(startDate, pageable);
    }

    public Page<MarketResponseDto> getAllMarkets(int page, int size, String name) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Market> markets = marketRepository.findAllMarkets(name, pageable);

        return markets.map(MarketResponseDto::toDto);
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

        Market market = new Market(
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
    public MarketResponseDto tradeMarket(Long userId, MarketTradeRequestDto tradeRequestDto) {

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
            throw new RuntimeException("돈부족");
        }

        User seller = market.getUser();

        // 구매자는 인벤에 아이템 증가 판매자/마켓은 감소
        market.decreaseAmount(buyAmount);
        updateInventory(item, seller, buyAmount*-1);
        updateInventory(item, buyer, buyAmount);

        return MarketResponseDto.toDto(market);
    }

    public void deleteMarket(Long userId, Long marketId) {

        User user = findUserById(userId);
        Market market = findMarketById(marketId);
        market.validateUser(user);

        market.delete();

    }

    /*
    여기서 부터 헬퍼 클래스
     */

    private void updateInventory(Item item, User user, int amount) {

        Inventory inventory = inventoryRepository.findByUserIdAndItemId(user.getId(), item.getId())
            .orElse(null);

        // 아이템 갖고있지 않았다면 판매자는 예외 구매자는 새로 객체 생성
        if (inventory == null) {

            if (amount < 0) {
                throw new RuntimeException("판매자의 재고가 부족합니다.");
            }
            inventory = new Inventory(item, user, amount);
        }

        // 구매자는 아이템 수 증가 판매자는 감소 후 setter 호출
        else {
            int newAmount = inventory.getAmount() + amount;
            if (newAmount < 0) {
                throw new RuntimeException("사용자 " + user.getId() + "의 재고가 부족합니다.");
            }
            inventory.setAmount(newAmount);
        }
        inventoryRepository.save(inventory);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저업슴"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("아이템업슴"));
    }

    private Market findMarketById(Long marketId) {
        return marketRepository.findById(marketId)
            .orElseThrow(() -> new RuntimeException("마켓업슴"));
    }

    private Inventory findInventoryByUserIdAndItemId(Long userId, Long itemId) {
        return inventoryRepository.findByUserIdAndItemId(userId, itemId)
            .orElseThrow(() -> new RuntimeException("인벤업슴"));
    }

}
