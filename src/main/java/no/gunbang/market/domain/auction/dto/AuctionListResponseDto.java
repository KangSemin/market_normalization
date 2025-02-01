package no.gunbang.market.domain.auction.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.auction.entity.Auction;

@Builder
@Getter
@AllArgsConstructor
public class AuctionListResponseDto {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private long startPrice;
    private long currentMaxPrice;
    private LocalDateTime dueDate;

    public static AuctionListResponseDto toDto(Auction auction, long currentMaxPrice) {
        return AuctionListResponseDto.builder()
            .auctionId(auction.getId())
            .itemId(auction.getItem().getId())
            .itemName(auction.getItem().getName())
            .startPrice(auction.getStartingPrice())
            .currentMaxPrice(currentMaxPrice)
            .dueDate(auction.getDueDate())
            .build();
    }
}
