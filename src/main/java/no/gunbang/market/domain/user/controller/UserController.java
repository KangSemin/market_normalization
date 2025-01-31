package no.gunbang.market.domain.user.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.user.dto.UserResponseDto;
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

    @GetMapping("/history/markets")
    public ResponseEntity<List<MarketHistoryResponseDto>> getHistoryMarkets(

    ){
        List<MarketHistoryResponseDto> marketHistory = userService.getMarketHistory();
        return ResponseEntity.ok(marketHistory);
    }

    @GetMapping("/history/auctions")
    public ResponseEntity<List<AuctionHistoryResponseDto>> getHistoryAuctions(

    ){
        List<AuctionHistoryResponseDto> auctionHistory = userService.getAuctionHistory();
        return ResponseEntity.ok(auctionHistory);
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(

    ){
        userService.getUser();
        return ResponseEntity.ok().build();
    }
}
