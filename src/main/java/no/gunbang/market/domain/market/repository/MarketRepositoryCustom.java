package no.gunbang.market.domain.market.repository;

import java.util.List;
import no.gunbang.market.domain.market.entity.Trade;

public interface MarketRepositoryCustom {

    List<Trade> findUserMarketHistory(Long userId);
}
