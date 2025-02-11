package no.gunbang.market.domain.auction.repository;

import java.time.LocalDateTime;
import java.util.List;

import no.gunbang.market.domain.auction.cursor.AuctionCursorValues;
import no.gunbang.market.domain.auction.dto.response.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidHistoryResponseDto;

public interface AuctionRepositoryCustom {

    List<BidHistoryResponseDto> findUserBidHistory(Long userId);

    List<AuctionHistoryResponseDto> findUserAuctionHistory(Long userId);

    List<AuctionListResponseDto> findPopularAuctionItems(LocalDateTime startDate, Long lastBidderCount, Long lastAuctionId);

    List<AuctionListResponseDto> findAllAuctionItems(
        LocalDateTime startDate,
        String searchKeyword,
        String sortBy,
        String sortDirection,
        Long lastAuctionId,
        AuctionCursorValues auctionCursorValues
    );
}

