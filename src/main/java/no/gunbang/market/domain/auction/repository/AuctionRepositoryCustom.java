package no.gunbang.market.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;
import no.gunbang.market.domain.auction.dto.response.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionRepositoryCustom {

    List<BidHistoryResponseDto> findUserBidHistory(Long userId);

    List<AuctionHistoryResponseDto> findUserAuctionHistory(Long userId);

    Page<AuctionListResponseDto> findPopularAuctionItems(LocalDateTime startDate, Pageable pageable);

    Page<AuctionListResponseDto> findAllAuctionItems(LocalDateTime startDate, String searchKeyword, String sortBy, String sortDirection, Pageable pageable);
}
