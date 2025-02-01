package no.gunbang.market.domain.market.repository;

import java.time.LocalDateTime;
import java.util.List;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketRepositoryCustom {

    Page<MarketResponseDto> findAllMarkets(String name, Pageable pageable);
    List<Trade> findUserMarketHistory(Long userId);

    Page<MarketListResponseDto> findPopularTradeItems(LocalDateTime startDate, Pageable pageable);
}
