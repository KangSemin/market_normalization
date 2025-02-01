package no.gunbang.market.domain.market.repository;

import java.time.LocalDateTime;
import java.util.List;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketRepositoryCustom {

    List<MarketHistoryResponseDto> findUserMarketHistory(Long userId);

    Page<MarketListResponseDto> findPopularMarketItems(LocalDateTime startDate, Pageable pageable);

    Page<MarketListResponseDto> findAllMarketItems(String searchKeyword, String sortBy, String sortDirection, Pageable pageable);
}
