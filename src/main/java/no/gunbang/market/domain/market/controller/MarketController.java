package no.gunbang.market.domain.market.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketRegisterRequestDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.dto.MarketTradeRequestDto;
import no.gunbang.market.domain.market.dto.MarketTradeResponseDto;
import no.gunbang.market.domain.market.service.MarketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private static final String PAGE_COUNT = "1";
    private static final String PAGE_SIZE = "10";

    private final MarketService marketService;

    @GetMapping("/populars")
    public ResponseEntity<Page<MarketListResponseDto>> getPopulars(
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size
    ){
        Pageable pageable = validatePageSize(page, size);
        Page<MarketListResponseDto> popularMarkets = marketService.getPopulars(pageable);
        return ResponseEntity.ok(popularMarkets);
    }

    @GetMapping
    public ResponseEntity<Page<MarketResponseDto>> getAllMarkets(
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size,
        @RequestParam(required = false) String name
    ) {
        Pageable pageable = validatePageSize(page, size);
        Page<MarketResponseDto> allMarkets = marketService.getAllMarkets(pageable, name);
        return ResponseEntity.ok(allMarkets);
    }

    @PostMapping
    public ResponseEntity<MarketResponseDto> registerMarket(
        @RequestBody MarketRegisterRequestDto registerRequestDto,
        HttpServletRequest req
    ) {
        Long sessionUserId = getSessionId(req);
        MarketResponseDto marketResponseDto = marketService.registerMarket(sessionUserId, registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(marketResponseDto);
    }

    @PostMapping("/trade")
    public ResponseEntity<MarketTradeResponseDto> tradeMarket(
        @RequestBody MarketTradeRequestDto tradeRequestDto,
        HttpServletRequest req
    ) {
        Long sessionUserId = getSessionId(req);
        MarketTradeResponseDto marketResponseDto = marketService.tradeMarket(sessionUserId, tradeRequestDto);
        return ResponseEntity.ok(marketResponseDto);
    }

    @DeleteMapping("/{marketId}")
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

    private Pageable validatePageSize(int page, int size) {
        if (page < 1 || size < 1) {
            throw new CustomException(ErrorCode.PAGING_ERROR);
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }
}
