package no.gunbang.market.domain.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionRegistrationRequestDto {

    private Long itemId;
    private long startingPrice;
    private int auctionDays;
}
