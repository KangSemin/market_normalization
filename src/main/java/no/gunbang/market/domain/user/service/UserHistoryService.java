package no.gunbang.market.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.response.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidHistoryResponseDto;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.market.dto.response.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.response.TradeHistoryResponseDto;
import no.gunbang.market.domain.market.repository.MarketRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final MarketRepository marketRepository;
    private final AuctionRepository auctionRepository;

    //경매장 판매
    public List<AuctionHistoryResponseDto> getAuctionHistory(Long userId) {
        return auctionRepository.findUserAuctionHistory(userId);
    }

    //경매장 입찰
    public List<BidHistoryResponseDto> getBidHistory(Long userId) {
        return auctionRepository.findUserBidHistory(userId);
    }

    //거래소 판매
    public List<MarketHistoryResponseDto> getMarketHistory(Long userId) {
        return marketRepository.findUserMarketHistory(userId);
    }

    //거래소 입찰
    public List<TradeHistoryResponseDto> getTradeHistory(Long userId) {
        return marketRepository.findUserTradeHistory(userId);
    }
}
