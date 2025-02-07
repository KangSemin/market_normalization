package no.gunbang.market.domain.market.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeHistoryResponseDto {

    private Long tradeId;
    private Long itemId;
    private String itemName;
    private int amount;
    private long totalPrice;
    private LocalDateTime createdAt;

    @QueryProjection
    public TradeHistoryResponseDto(Long tradeId, Long itemId, String itemName, int amount, long totalPrice,
        LocalDateTime createdAt) {
        this.tradeId = tradeId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }
}
