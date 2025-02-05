package no.gunbang.market.domain.market.repository;

import no.gunbang.market.domain.market.entity.Trade;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    default List<Trade> findRecentTrades(LocalDateTime timestamp) {
        return findByCreatedAtAfter(timestamp, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    List<Trade> findByCreatedAtAfter(LocalDateTime timestamp, Sort sort);
}