package no.gunbang.market.domain.market.repository;

import java.util.List;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.entity.MarketTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketRepositoryCustom {

    List<MarketTrade> findUserMarketHistory(Long userId);

    Page<Market> findAllMarkets(String name, Pageable pageable);
}
