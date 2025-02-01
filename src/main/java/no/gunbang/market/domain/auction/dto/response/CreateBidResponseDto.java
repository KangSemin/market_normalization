package no.gunbang.market.domain.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.auction.entity.Bid;

@Builder
@Getter
@AllArgsConstructor
public class CreateBidResponseDto {
    private Long auctionId;

    public static CreateBidResponseDto toDto(Bid bid){
        return CreateBidResponseDto.builder()
            .auctionId(bid.getAuction().getId())
            .build();
    }
}
