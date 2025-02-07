package no.gunbang.market.domain.market.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.entity.Status;

@Getter
@NoArgsConstructor
public class MarketHistoryResponseDto {

    private Long marketId;
    private Long itemId;
    private String itemName;
    private int amount;
    private long totalPrice;
    private Status status;
    private LocalDateTime createdAt;

    @QueryProjection
    public MarketHistoryResponseDto(Long marketId, Long itemId, String itemName, int amount, long totalPrice,
        Status status, LocalDateTime createdAt) {
        this.marketId = marketId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }
}
