package no.gunbang.market.domain.auction.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.request.CreateAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.CreateAuctionResponseDto;
import no.gunbang.market.domain.auction.service.AuctionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private static final String PAGE_COUNT = "1";
    private static final String PAGE_SIZE = "10";

    @PostMapping
    public ResponseEntity<CreateAuctionResponseDto> createAuction(
        @RequestBody CreateAuctionRequestDto requestDto,
        HttpSession httpSession
    ) {

        Long userId = (Long) httpSession.getAttribute("userId");

        CreateAuctionResponseDto responseDto = auctionService.saveAuction(
            requestDto,
            userId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/populars")
    public ResponseEntity<Page<AuctionListResponseDto>> getPopulars(
        @RequestParam(defaultValue = PAGE_COUNT) int page,
        @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = validatePageSize(page, size);
        Page<AuctionListResponseDto> popularAuctions = auctionService.getPopulars(pageable);
        return ResponseEntity.ok(popularAuctions);
    }

    private Pageable validatePageSize(int page, int size) {
        if (page < 1 || size < 1) {
            throw new CustomException(ErrorCode.PAGING_ERROR);
        }
        return PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    }
}
