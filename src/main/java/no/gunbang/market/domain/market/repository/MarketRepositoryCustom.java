package no.gunbang.market.domain.market.repository;

import java.util.List;
import no.gunbang.market.domain.market.entity.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import no.gunbang.market.domain.market.entity.Trade;

public interface MarketRepositoryCustom {

    Page<Market> findAllMarkets(String name, Pageable pageable);
    List<Trade> findUserMarketHistory(Long userId);
}
