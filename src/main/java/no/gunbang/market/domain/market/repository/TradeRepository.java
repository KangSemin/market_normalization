package no.gunbang.market.domain.market.repository;

import no.gunbang.market.domain.market.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
