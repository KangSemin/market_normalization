package no.gunbang.market.domain.auction.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.Status;

@Getter
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

    @QueryProjection
    public AuctionHistoryResponseDto(Long auctionId, Long itemId, String itemName, long startPrice, long userBidPrice,
        long currentMaxPrice, String bidStatus, String saleStatus, boolean isSeller,
        LocalDateTime dueDate, Status status) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.startPrice = startPrice;
        this.userBidPrice = userBidPrice;
        this.currentMaxPrice = currentMaxPrice;
        this.bidStatus = bidStatus;
        this.saleStatus = saleStatus;
        this.isSeller = isSeller;
        this.dueDate = dueDate;
        this.status = status;
    }
}
