package no.gunbang.market.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionRepositoryCustom {

    List<Auction> findUserAuctionHistory(Long userId);

    Page<AuctionListResponseDto> findPopularBidItems(LocalDateTime startDate, Pageable pageable);
}
