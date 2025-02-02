package no.gunbang.market.domain.auction.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.request.CreateAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.request.CreateBidRequestDto;
import no.gunbang.market.domain.auction.dto.response.CreateAuctionResponseDto;
import no.gunbang.market.domain.auction.dto.response.CreateBidResponseDto;
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
    public ResponseEntity<Page<AuctionListResponseDto>> getPopulars(
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = validatePageSize(page, size);
        Page<AuctionListResponseDto> popularAuctions = auctionService.getPopulars(pageable);
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
        Page<AuctionListResponseDto> allMarkets = auctionService.getAllAuctions(pageable, searchKeyword, sortBy, sortDirection);
        return ResponseEntity.ok(allMarkets);
    }

    @PostMapping
    public ResponseEntity<CreateAuctionResponseDto> createAuction(
        @RequestBody CreateAuctionRequestDto requestDto,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        CreateAuctionResponseDto responseDto = auctionService.saveAuction(
            requestDto,
            sessionUserId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/bids")
    public ResponseEntity<CreateBidResponseDto> createBid(
        @RequestBody CreateBidRequestDto requestDto,
        HttpServletRequest request
    ) {
        Long sessionUserId = getSessionId(request);

        CreateBidResponseDto responseDto = auctionService.participateInAuction(
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

    private Pageable validatePageSize(int page, int size) {
        if (page < 1 || size < 1) {
            throw new CustomException(ErrorCode.PAGING_ERROR);
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }

    private Long getSessionId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("userId");
    }
}
