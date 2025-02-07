package no.gunbang.market;

import java.util.Map;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.market.entity.TradeCount;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.repository.TradeCountRepository;
import no.gunbang.market.domain.market.repository.TradeRepository;
import no.gunbang.market.domain.market.service.TradeCountScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeCountSchedulerTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeCountRepository tradeCountRepository;

    @InjectMocks
    private TradeCountScheduler tradeCountScheduler;

    @Test
    void updateTradeCount_정상동작() {
        // Given
        LocalDateTime recentTime = LocalDateTime.now().minusMinutes(2);

        Trade mockTrade1 = TestUtils.spy(Trade.class, Map.of(
            "id", 1001L,
            "market", mockMarket(1L),
            "amount", 2,
            "totalPrice", 5000,
            "createdAt", recentTime
        ));

        Trade mockTrade2 = TestUtils.spy(Trade.class, Map.of(
            "id", 1002L,
            "market", mockMarket(2L),
            "amount", 3,
            "totalPrice", 8000,
            "createdAt", recentTime
        ));

        when(tradeRepository.findRecentTrades(any(LocalDateTime.class)))
            .thenReturn(List.of(mockTrade1, mockTrade2, mockTrade1));

        when(tradeCountRepository.findById(1L)).thenReturn(Optional.of(mockTradeCount(1L, 5)));
        when(tradeCountRepository.findById(2L)).thenReturn(Optional.empty());

        // When
        tradeCountScheduler.updateTradeCount();

        // Then
        ArgumentCaptor<TradeCount> tradeCountCaptor = ArgumentCaptor.forClass(TradeCount.class);
        verify(tradeCountRepository, times(2)).save(tradeCountCaptor.capture());

        List<TradeCount> capturedTradeCounts = tradeCountCaptor.getAllValues();
        assertEquals(2, capturedTradeCounts.size());

        TradeCount updatedCount1 = capturedTradeCounts.get(0);
        assertEquals(1L, updatedCount1.getItemId());
        assertEquals(5 + 2, updatedCount1.getCount());

        TradeCount newCount2 = capturedTradeCounts.get(1);
        assertEquals(2L, newCount2.getItemId());
        assertEquals(1, newCount2.getCount());

        verify(tradeRepository, times(1)).findRecentTrades(any(LocalDateTime.class));
        verify(tradeCountRepository, times(2)).findById(anyLong());
        verify(tradeCountRepository, times(2)).save(any(TradeCount.class));
    }

    private TradeCount mockTradeCount(Long itemId, int count) {
        return TestUtils.spy(TradeCount.class, Map.of(
            "itemId", itemId,
            "count", count
        ));
    }

    private Market mockMarket(Long itemId) {
        Item item = TestUtils.spy(Item.class, Map.of(
            "id", itemId
        ));

        return TestUtils.spy(Market.class, Map.of(
            "item", item
        ));
    }
}
