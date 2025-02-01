package no.gunbang.market.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.response.AuctionHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
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

    @GetMapping("/history/auctions")
    public ResponseEntity<List<AuctionHistoryResponseDto>> getHistoryAuctions(
        HttpServletRequest req
    ){
        Long sessionUserId = getSessionId(req);
        List<AuctionHistoryResponseDto> auctionHistory = userHistoryService.getAuctionHistory(sessionUserId);
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

    private Long getSessionId(HttpServletRequest req) {
        return (Long) req.getSession().getAttribute("userId");
    }
}
