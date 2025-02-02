package no.gunbang.market.domain.auction.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.entity.Auction;

@Builder
@Getter
@AllArgsConstructor
public class AuctionRegistrationResponseDto {

    private Long auctionId;
    private Long itemId;
    private long startingPrice;
    private LocalDateTime dueDate;
    private Status status;

    public static AuctionRegistrationResponseDto toDto(Auction auction) {
        return AuctionRegistrationResponseDto.builder()
            .auctionId(auction.getId())
            .itemId(auction.getItem().getId())
            .startingPrice(auction.getStartingPrice())
            .dueDate(auction.getDueDate())
            .status(Status.ON_SALE)
            .build();
    }
}
