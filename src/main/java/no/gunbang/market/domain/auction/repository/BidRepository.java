package no.gunbang.market.domain.auction.repository;

import no.gunbang.market.domain.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {

}
