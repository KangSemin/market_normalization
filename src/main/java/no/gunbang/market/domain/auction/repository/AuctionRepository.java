package no.gunbang.market.domain.auction.repository;

import no.gunbang.market.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
