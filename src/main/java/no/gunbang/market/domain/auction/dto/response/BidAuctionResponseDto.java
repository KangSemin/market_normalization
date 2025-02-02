package no.gunbang.market.domain.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.auction.entity.Bid;

@Builder
@Getter
@AllArgsConstructor
public class BidAuctionResponseDto {
    private Long auctionId;

    public static BidAuctionResponseDto toDto(Bid bid){
        return BidAuctionResponseDto.builder()
            .auctionId(bid.getAuction().getId())
            .build();
    }
}
