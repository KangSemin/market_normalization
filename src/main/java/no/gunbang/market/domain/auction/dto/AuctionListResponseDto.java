package no.gunbang.market.domain.auction.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionListResponseDto {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private long startPrice;
    private long currentMaxPrice;
    private LocalDateTime dueDate;
    private long bidCount;
}
