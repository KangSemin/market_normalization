package no.gunbang.market.domain.auction.controller;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.request.CreateAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.CreateAuctionResponseDto;
import no.gunbang.market.domain.auction.service.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<CreateAuctionResponseDto> createAuction(
        @RequestBody CreateAuctionRequestDto requestDto
    ) {
        CreateAuctionResponseDto responseDto = auctionService.saveAuction(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
