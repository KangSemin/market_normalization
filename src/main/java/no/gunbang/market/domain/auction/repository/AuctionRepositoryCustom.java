package no.gunbang.market.domain.auction.repository;

import java.util.List;
import no.gunbang.market.domain.auction.entity.Auction;

public interface AuctionRepositoryCustom {

    List<Auction> findUserAuctionHistory(Long userId);
}
