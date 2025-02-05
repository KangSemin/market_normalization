package no.gunbang.market.domain.market.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.cursor.MarketCursorValues;
import no.gunbang.market.domain.market.dto.*;
import no.gunbang.market.domain.market.dto.MarketPopularResponseDto;
import no.gunbang.market.domain.market.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/markets")
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/populars")
    public ResponseEntity<List<MarketPopularResponseDto>> getPopulars(
        @RequestParam(required = false) Long lastItemId
    ) {
        List<MarketPopularResponseDto> popularMarkets = marketService.getPopulars(lastItemId);
        return ResponseEntity.ok(popularMarkets);
    }

    @GetMapping("/main")
    public ResponseEntity<List<MarketListResponseDto>> getAllMarkets(
        @RequestParam(required = false) String searchKeyword,
        @RequestParam(required = false, defaultValue = "default") String sortBy,
        @RequestParam(required = false, defaultValue = "DESC") String sortDirection,
        @RequestParam(required = false) Long lastItemId,
        @RequestParam(required = false) Long lastPrice,
        @RequestParam(required = false) Long lastAmount
    ) {
        MarketCursorValues marketCursorValues = new MarketCursorValues(lastPrice, lastAmount);
        List<MarketListResponseDto> items = marketService.getAllMarkets(
                searchKeyword, sortBy, sortDirection, lastItemId, marketCursorValues
        );
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<List<MarketResponseDto>> getSameItems(
        @PathVariable("itemId") Long itemId
    ) {
        List<MarketResponseDto> sameItems = marketService.getSameItems(itemId);
        return ResponseEntity.ok(sameItems);
    }

    @PostMapping
    public ResponseEntity<MarketResponseDto> registerMarket(
        @RequestBody MarketRegistrationRequestDto requestDto,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        MarketResponseDto responseDto = marketService.registerMarket(
            sessionUserId,
            requestDto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/trades")
    public ResponseEntity<List<MarketTradeResponseDto>> tradeMarket(
        @RequestBody MarketTradeRequestDto requestDto,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        List<MarketTradeResponseDto> responseDto = marketService.tradeMarket(
            sessionUserId,
            requestDto
        );

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{marketId}")
    public ResponseEntity<String> deleteMarket(
        @PathVariable("marketId") Long marketId,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        marketService.deleteMarket(sessionUserId, marketId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("삭제 완료");
    }

    private Long getSessionId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("userId");
    }
}
