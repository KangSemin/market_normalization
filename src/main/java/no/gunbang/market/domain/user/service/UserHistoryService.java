package no.gunbang.market.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.market.repository.MarketRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final MarketRepository marketRepository;
    private final AuctionRepository auctionRepository;

    public List<AuctionHistoryResponseDto> getAuctionHistory(Long userId) {
        List<Auction> auctions = auctionRepository.findUserAuctionHistory(userId);

        return auctions.stream()
            .map(auction -> AuctionHistoryResponseDto.toDto(
                auction,
                0L,
                null,
                false,
                auction.getUser().getId().equals(userId)
            )).toList();
    }

    public List<MarketHistoryResponseDto> getMarketHistory(Long userId) {
        List<Trade> trades = marketRepository.findUserMarketHistory(userId);

        return trades.stream()
            .map(trade -> MarketHistoryResponseDto.toDto(trade, userId))
            .toList();
    }

}
