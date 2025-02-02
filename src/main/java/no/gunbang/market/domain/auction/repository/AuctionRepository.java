package no.gunbang.market.domain.auction.repository;

import java.util.Collection;
import java.util.Optional;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {

    Optional<Auction> findByIdAndStatus(Long id, Status status);
}