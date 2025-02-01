package no.gunbang.market.domain.market.repository;

import java.time.LocalDateTime;
import java.util.List;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketRepositoryCustom {

    List<MarketHistoryResponseDto> findUserMarketHistory(Long userId);
    Page<MarketResponseDto> findAllMarkets(String name, Pageable pageable);

    Page<MarketListResponseDto> findPopularMarketItems(LocalDateTime startDate, Pageable pageable);

    Page<MarketListResponseDto> findAllMarketItems(Pageable pageable);
}
