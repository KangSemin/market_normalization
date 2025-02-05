package no.gunbang.market.domain.auction.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Bid> findWithLockByAuction(Auction auction);

    Optional<Bid> findByAuction(Auction auction);

    boolean existsByAuctionId(Long auctionId);

    @Transactional
    void deleteAllByAuction(Auction auction);
}