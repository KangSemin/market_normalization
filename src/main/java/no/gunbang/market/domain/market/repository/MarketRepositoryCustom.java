package no.gunbang.market.domain.market.repository;

import java.time.LocalDateTime;
import java.util.List;

import no.gunbang.market.domain.market.cursor.MarketCursorValues;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketPopularResponseDto;
import no.gunbang.market.domain.market.dto.TradeHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketRepositoryCustom {

    List<TradeHistoryResponseDto> findUserTradeHistory(Long userId);

    List<MarketHistoryResponseDto> findUserMarketHistory(Long userId);

    List<MarketPopularResponseDto> findPopularMarketItems(LocalDateTime startDate, Long lastTradeCount, Long lastItemId);

    List<MarketListResponseDto> findAllMarketItems(
            String searchKeyword,
            String sortBy,
            String sortDirection,
            Long lastItemId,
            MarketCursorValues marketCursorValues
    );

    Page<MarketPopularResponseDto> findPopularMarketItemstest(LocalDateTime startDate, Pageable pageable);

    Page<MarketListResponseDto> findAllMarketItemstest(String searchKeyword, String sortBy, String sortDirection, Pageable pageable);
}
