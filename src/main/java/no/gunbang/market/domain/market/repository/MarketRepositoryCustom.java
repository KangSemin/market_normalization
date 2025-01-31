package no.gunbang.market.domain.market.repository;

import java.util.List;
import no.gunbang.market.domain.market.entity.MarketTrade;

public interface MarketRepositoryCustom {

    List<MarketTrade> findUserMarketHistory(Long userId);
}
