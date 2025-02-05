package no.gunbang.market.domain.market.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.market.dto.*;
import no.gunbang.market.domain.market.dto.MarketPopularResponseDto;
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
    public ResponseEntity<List<MarketPopularResponseDto>> getPopulars(
        @RequestParam(required = false) Long lastTradeCount,
        @RequestParam(required = false) Long lastItemId
    ) {
        validateCursorParams(lastTradeCount, lastItemId);
        List<MarketPopularResponseDto> popularMarkets = marketService.getPopulars(lastTradeCount, lastItemId);
        return ResponseEntity.ok(popularMarkets);
    }

    @GetMapping("/main")
    public ResponseEntity<Page<MarketListResponseDto>> getAllMarkets(
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size,
        @RequestParam(required = false) String searchKeyword,
        @RequestParam(defaultValue = "random") String sortBy,
        @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Pageable pageable = validatePageSize(page, size);
        Page<MarketListResponseDto> allMarkets = marketService.getAllMarkets(pageable,
            searchKeyword, sortBy, sortDirection);
        return ResponseEntity.ok(allMarkets);
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

    /**
     * lastTradeCount 와 lastAuctionId가 둘 다 있거나, 둘 다 없어야 하는지 검사하는 메서드
     */
    private void validateCursorParams(Long lastTradeCount, Long lastItemId) {
        if ((lastTradeCount == null && lastItemId != null) || (lastTradeCount != null && lastItemId == null)) {
            throw new CustomException(ErrorCode.BAD_PARAMETER);
        }
    }

    private Pageable validatePageSize(int page, int size) {
        if (page < 1 || size < 1) {
            throw new CustomException(ErrorCode.PAGING_ERROR);
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }
}
