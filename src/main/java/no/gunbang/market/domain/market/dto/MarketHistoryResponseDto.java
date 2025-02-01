package no.gunbang.market.domain.market.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
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
}
