package no.gunbang.market.domain.market.repository;

import java.util.List;

public interface MarketRepositoryCustom {

    List<MarketTrade> findUserMarketHistory(Long userId);
}
