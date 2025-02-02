package no.gunbang.market.domain.auction.repository;

import java.util.Optional;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findByAuction(Auction auction);

    boolean existsByAuctionId(Long auctionId);
}