package no.gunbang.market.domain.market.repository;

import java.time.LocalDateTime;
import java.util.List;

import no.gunbang.market.domain.market.cursor.MarketCursorValues;
import no.gunbang.market.domain.market.dto.response.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.response.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.response.MarketPopularResponseDto;
import no.gunbang.market.domain.market.dto.response.TradeHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketRepositoryCustom {

    List<TradeHistoryResponseDto> findUserTradeHistory(Long userId);

    List<MarketHistoryResponseDto> findUserMarketHistory(Long userId);

    List<MarketPopularResponseDto> findPopularMarketItemsCursor(LocalDateTime startDate, Long lastTradeCount, Long lastItemId);

    List<MarketListResponseDto> findAllMarketItemsCursor(
        String searchKeyword,
        String sortBy,
        String sortDirection,
        Long lastItemId,
        MarketCursorValues marketCursorValues
    );

    Page<MarketPopularResponseDto> findPopularMarketItems(LocalDateTime startDate, Pageable pageable);

    Page<MarketListResponseDto> findAllMarketItems(String searchKeyword, String sortBy, String sortDirection, Pageable pageable);

}