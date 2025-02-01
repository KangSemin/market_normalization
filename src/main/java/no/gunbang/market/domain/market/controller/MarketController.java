package no.gunbang.market.domain.market.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.dto.MarketTradeRequestDto;
import no.gunbang.market.domain.market.dto.MarketRegisterRequestDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.service.MarketService;
import org.springframework.data.domain.Page;
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

    @GetMapping
    public ResponseEntity<Page<MarketResponseDto>> getAllMarkets(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String name
    ) {
        Page<MarketResponseDto> allMarkets = marketService.getAllMarkets(page, size, name);
        return ResponseEntity.ok(allMarkets);
    }

    @PostMapping("/items")
    public ResponseEntity<MarketResponseDto> registerMarket(
        @RequestBody MarketRegisterRequestDto registerRequestDto,
        HttpServletRequest req
    ) {
        Long sessionUserId = getSessionId(req);
        MarketResponseDto marketResponseDto = marketService.registerMarket(sessionUserId, registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(marketResponseDto);
    }

    @PostMapping("/items/trade")
    public ResponseEntity<MarketResponseDto> tradeMarket(
        @RequestParam MarketTradeRequestDto tradeRequestDto,
        HttpServletRequest req
    ) {
        Long sessionUserId = getSessionId(req);
        MarketResponseDto marketResponseDto = marketService.tradeMarket(sessionUserId, tradeRequestDto);
        return ResponseEntity.ok(marketResponseDto);
    }

    @DeleteMapping("/items/{marketId}")
    public ResponseEntity<String> deleteMarket(
        @PathVariable Long marketId,
        HttpServletRequest req
    ) {
        Long sessionUserId = getSessionId(req);
        marketService.deleteMarket(sessionUserId, marketId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("삭제완료");
    }


    private Long getSessionId(HttpServletRequest req) {
        return (Long) req.getSession().getAttribute("userId");
    }
}
