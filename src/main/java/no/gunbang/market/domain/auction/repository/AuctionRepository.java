package no.gunbang.market.domain.auction.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Auction> findByIdAndStatus(
        Long id,
        Status status
    );

    List<Auction> findByDueDateBeforeAndStatus(
        LocalDateTime now,
        Status status
    );



    @Transactional
    void delete(Auction auction);
}