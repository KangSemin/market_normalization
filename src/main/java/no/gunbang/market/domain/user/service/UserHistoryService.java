package no.gunbang.market.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.entity.MarketTrade;
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
        List<MarketTrade> marketTrades = marketRepository.findUserMarketHistory(userId);

        return marketTrades.stream()
            .map(marketTrade -> MarketHistoryResponseDto.toDto(
                marketTrade.getMarket(),
                marketTrade,
                marketTrade.getTrade(),
                userId
            )).toList();
    }

}
