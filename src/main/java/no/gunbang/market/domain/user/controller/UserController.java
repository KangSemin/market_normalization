package no.gunbang.market.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.response.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidHistoryResponseDto;
import no.gunbang.market.domain.market.dto.response.TradeHistoryResponseDto;
import no.gunbang.market.domain.market.dto.response.MarketHistoryResponseDto;
import no.gunbang.market.domain.user.dto.UserResponseDto;
import no.gunbang.market.domain.user.service.UserHistoryService;
import no.gunbang.market.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserHistoryService userHistoryService;

    @GetMapping("/history/markets")
    public ResponseEntity<List<MarketHistoryResponseDto>> getHistoryMarkets(
        HttpServletRequest req
    ){
        Long sessionUserId = getSessionId(req);
        List<MarketHistoryResponseDto> marketHistory = userHistoryService.getMarketHistory(sessionUserId);
        return ResponseEntity.ok(marketHistory);
    }

    @GetMapping("/history/trades")
    public ResponseEntity<List<TradeHistoryResponseDto>> getHistoryTrades(
        HttpServletRequest req
    ){
        Long sessionUserId = getSessionId(req);
        List<TradeHistoryResponseDto> marketHistory = userHistoryService.getTradeHistory(sessionUserId);
        return ResponseEntity.ok(marketHistory);
    }

    @GetMapping("/history/auctions")
    public ResponseEntity<List<AuctionHistoryResponseDto>> getHistoryAuctions(
        HttpServletRequest req
    ){
        Long sessionUserId = getSessionId(req);
        List<AuctionHistoryResponseDto> auctionHistory = userHistoryService.getAuctionHistory(sessionUserId);
        return ResponseEntity.ok(auctionHistory);
    }

    @GetMapping("/history/bids")
    public ResponseEntity<List<BidHistoryResponseDto>> getHistoryBids(
        HttpServletRequest req
    ){
        Long sessionUserId = getSessionId(req);
        List<BidHistoryResponseDto> auctionHistory = userHistoryService.getBidHistory(sessionUserId);
        return ResponseEntity.ok(auctionHistory);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(
        HttpServletRequest req
    ){
        Long sessionUserId = getSessionId(req);
        UserResponseDto user = userService.getUser(sessionUserId);
        return ResponseEntity.ok(user);
    }

    /*
    helper
     */
    private Long getSessionId(HttpServletRequest req) {
        return (Long) req.getSession().getAttribute("userId");
    }
}
