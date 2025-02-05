package no.gunbang.market.domain.market.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import no.gunbang.market.domain.market.entity.TradeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TradeCountRepository extends JpaRepository<TradeCount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) //동시 수정 방지
    Optional<TradeCount> findById(Long itemId);
}
