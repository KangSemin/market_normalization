package no.gunbang.market.domain.auction.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.auction.entity.Auction;

@Builder
@Getter
@AllArgsConstructor
public class AuctionResponseDto {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private long currentMaxPrice;
    private LocalDateTime dueDate;
    private int bidderCount;

    public static AuctionResponseDto toDto(
        Auction auction,
        long currentMaxPrice
    ) {
        return AuctionResponseDto.builder()
            .auctionId(auction.getId())
            .itemId(auction.getItem().getId())
            .itemName(auction.getItem().getName())
            .currentMaxPrice(currentMaxPrice)
            .dueDate(auction.getDueDate())
            .bidderCount(auction.getBidderCount())
            .build();

    }
}