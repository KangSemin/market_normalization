package no.gunbang.market.domain.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BidAuctionRequestDto {
    private Long auctionId;
    private long bidPrice;
}