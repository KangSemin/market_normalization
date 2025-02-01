package no.gunbang.market.domain.market.controller;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.service.MarketService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
