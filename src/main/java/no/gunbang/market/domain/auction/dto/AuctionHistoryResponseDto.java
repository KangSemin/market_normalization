package no.gunbang.market.domain.auction.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.Status;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuctionHistoryResponseDto {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private long startPrice;
    private long userBidPrice;
    private long currentMaxPrice;
    private String bidStatus;
    private String saleStatus;
    private boolean isSeller;
    private LocalDateTime dueDate;
    private Status status;
}
