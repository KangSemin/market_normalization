package no.gunbang.market.domain.auction.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.request.AuctionRegistrationRequestDto;
import no.gunbang.market.domain.auction.dto.request.BidAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionRegistrationResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidAuctionResponseDto;
import no.gunbang.market.domain.auction.service.AuctionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private static final String PAGE_COUNT = "1";
    private static final String PAGE_SIZE = "10";

    private final AuctionService auctionService;

    @GetMapping("/populars")
    public ResponseEntity<List<AuctionListResponseDto>> getPopulars(
        @RequestParam(required = false) Long lastBidderCount,
        @RequestParam(required = false) Long lastAuctionId
    ) {
        validateCursorParams(lastBidderCount, lastAuctionId);
        List<AuctionListResponseDto> popularAuctions = auctionService.getPopulars(lastBidderCount, lastAuctionId);
        return ResponseEntity.ok(popularAuctions);
    }

    @GetMapping("/main")
    public ResponseEntity<Page<AuctionListResponseDto>> getAllAuctions(
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size,
        @RequestParam(required = false) String searchKeyword,
        @RequestParam(defaultValue = "random") String sortBy,
        @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Pageable pageable = validatePageSize(page, size);
        Page<AuctionListResponseDto> allMarkets = auctionService.getAllAuctions(pageable,
            searchKeyword, sortBy, sortDirection);
        return ResponseEntity.ok(allMarkets);
    }

    @PostMapping
    public ResponseEntity<AuctionRegistrationResponseDto> registerAuction(
        @RequestBody AuctionRegistrationRequestDto requestDto,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        AuctionRegistrationResponseDto responseDto = auctionService.registerAuction(
            sessionUserId,
            requestDto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionResponseDto> getAuctionById(
        @PathVariable("auctionId") Long auctionId
    ) {
        AuctionResponseDto responseDto = auctionService.getAuctionById(auctionId);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/bids")
    public ResponseEntity<BidAuctionResponseDto> bidAuction(
        @RequestBody BidAuctionRequestDto requestDto,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        BidAuctionResponseDto responseDto = auctionService.bidAuction(
            sessionUserId,
            requestDto
        );

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<String> deleteAuction(
        @PathVariable("auctionId") Long auctionId,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        auctionService.deleteAuction(sessionUserId, auctionId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("삭제 완료");
    }

    private Long getSessionId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("userId");
    }

    /**
     * lastBidderCount 와 lastAuctionId 가 둘 다 있거나, 둘 다 없어야 하는지 검사하는 메서드
     */
    private void validateCursorParams(Long lastBidderCount, Long lastAuctionId) {
        if ((lastBidderCount == null && lastAuctionId != null) || (lastBidderCount != null && lastAuctionId == null)) {
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
