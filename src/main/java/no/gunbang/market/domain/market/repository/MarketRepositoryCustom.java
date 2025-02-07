package no.gunbang.market.domain.market.repository;

import java.time.LocalDateTime;
import java.util.List;

import no.gunbang.market.domain.market.cursor.MarketCursorValues;
import no.gunbang.market.domain.market.dto.response.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.response.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.response.MarketPopularResponseDto;
import no.gunbang.market.domain.market.dto.response.TradeHistoryResponseDto;

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
}