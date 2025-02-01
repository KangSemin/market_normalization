package no.gunbang.market.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionRepositoryCustom {

    List<AuctionHistoryResponseDto> findUserAuctionHistory(Long userId);

    Page<AuctionListResponseDto> findPopularBidItems(LocalDateTime startDate, Pageable pageable);
}
