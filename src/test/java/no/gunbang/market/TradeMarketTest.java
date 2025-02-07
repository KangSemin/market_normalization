package no.gunbang.market;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.entity.InventoryRepository;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.common.entity.ItemRepository;
import no.gunbang.market.common.entity.Status;
import no.gunbang.market.domain.market.dto.request.MarketTradeRequestDto;
import no.gunbang.market.domain.market.dto.response.MarketTradeResponseDto;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.market.repository.MarketRepository;
import no.gunbang.market.domain.market.repository.TradeRepository;
import no.gunbang.market.domain.market.service.MarketService;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TradeMarketTest {

    @Mock
    private MarketRepository marketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private MarketService marketService;


    @Test
    void tradeMarket_거래요청_수량보다_적은_거래소1물량_부분거래_이후_다음_거래소2에서_나머지거래() {
        // given
        Long buyerId = 1L;
        Long itemId = 100L;
        int requestAmount = 10;
        int availableAmount1 = 5;
        int availableAmount2 = 5;

        MarketTradeRequestDto requestDto = TestUtils.spy(MarketTradeRequestDto.class, Map.of(
            "itemId", itemId,
            "amount", requestAmount
        ));

        User buyer = TestUtils.spy(User.class, Map.of(
            "id", 1L,
            "nickname", "구매자",
            "email", "buyer@example.com",
            "gold", 20000L
        ));

        User seller1 = TestUtils.spy(User.class, Map.of(
            "id", 50L,
            "nickname", "판매자1",
            "email", "seller1@example.com",
            "gold", 5000L
        ));

        User seller2 = TestUtils.spy(User.class, Map.of(
            "id", 51L,
            "nickname", "판매자2",
            "email", "seller2@example.com",
            "gold", 7000L
        ));

        Item item = TestUtils.spy(Item.class, Map.of(
            "id", itemId,
            "name", "테스트 아이템"
        ));

        Market market1 = TestUtils.spy(Market.class, Map.of(
            "id", 100L,
            "amount", availableAmount1,
            "price", 1000L,
            "status", Status.ON_SALE,
            "user", seller1,
            "item", item,
            "createdAt", LocalDateTime.now()
        ));

        Market market2 = TestUtils.spy(Market.class, Map.of(
            "id", 101L,
            "amount", availableAmount2,
            "price", 1100L,
            "status", Status.ON_SALE,
            "user", seller2,
            "item", item,
            "createdAt", LocalDateTime.now()
        ));

        Trade trade1 = TestUtils.spy(Trade.class, Map.of(
            "id", 200L,
            "user", buyer,
            "market", market1,
            "amount", availableAmount1,
            "totalPrice", availableAmount1 * 1000L
        ));

        Trade trade2 = TestUtils.spy(Trade.class, Map.of(
            "id", 201L,
            "user", buyer,
            "market", market2,
            "amount", availableAmount2,
            "totalPrice", availableAmount2 * 1100L
        ));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findByIdForUpdate(buyerId)).thenReturn(buyer);
        when(marketRepository.findByItemIdOrderByPriceAscCreatedAtAsc(itemId))
            .thenReturn(List.of(market1, market2));

        when(marketRepository.findByIdForUpdate(market1.getId())).thenReturn(market1);
        when(marketRepository.findByIdForUpdate(market2.getId())).thenReturn(market2);
        when(userRepository.findByIdForUpdate(market1.getUser().getId())).thenReturn(seller1);
        when(userRepository.findByIdForUpdate(market2.getUser().getId())).thenReturn(seller2);
        when(tradeRepository.save(any(Trade.class)))
            .thenReturn(trade1)
            .thenReturn(trade2);

        //when
        List<MarketTradeResponseDto> response = marketService.tradeMarket(buyerId, requestDto);

        //then
        assertThat(response).hasSize(2);
        verify(market1, times(1)).decreaseAmount(availableAmount1);
        verify(market2, times(1)).decreaseAmount(availableAmount2);
        verify(buyer, times(1)).decreaseGold(availableAmount1 * 1000L);
        verify(buyer, times(1)).decreaseGold(availableAmount2 * 1100L);
        verify(seller1, times(1)).increaseGold(availableAmount1 * 1000L);
        verify(seller2, times(1)).increaseGold(availableAmount2 * 1100L);
        verify(tradeRepository, times(2)).save(any(Trade.class));
    }


}
