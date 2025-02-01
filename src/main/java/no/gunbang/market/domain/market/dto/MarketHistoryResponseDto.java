package no.gunbang.market.domain.market.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketHistoryResponseDto {

    private Long marketId;
    private Long itemId;
    private String itemName;
    private int amount;
    private long totalPrice;
    private String userRole;
    private String transactionStatus;
    private LocalDateTime transactionDate;

    @QueryProjection
    public MarketHistoryResponseDto(Long marketId, Long itemId, String itemName, int amount, long totalPrice,
        String userRole, String transactionStatus, LocalDateTime transactionDate) {
        this.marketId = marketId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.userRole = userRole;
        this.transactionStatus = transactionStatus;
        this.transactionDate = transactionDate;
    }
}
