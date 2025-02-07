package no.gunbang.market.domain.auction.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.entity.Status;

@Getter
@NoArgsConstructor
public class BidHistoryResponseDto {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private long currentMaxPrice;
    private LocalDateTime dueDate;
    private Status status;

    @QueryProjection
    public BidHistoryResponseDto(Long auctionId, Long itemId, String itemName,
        long currentMaxPrice, LocalDateTime dueDate, Status status) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentMaxPrice = currentMaxPrice;
        this.dueDate = dueDate;
        this.status = status;
    }
}
