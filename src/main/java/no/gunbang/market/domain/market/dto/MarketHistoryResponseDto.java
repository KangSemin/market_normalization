package no.gunbang.market.domain.market.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.market.entity.Market;

@Builder
@Getter
@AllArgsConstructor
public class MarketHistoryResponseDto {

    private Long marketId;
    private Long itemId;
    private String itemName;
    private int amount;
    private long totalPrice;
    private String userRole;
    private String transactionStatus;
    private LocalDateTime transactionDate;

    public static MarketHistoryResponseDto toDto(Trade trade, Long userId) {
        Market market = trade.getMarket();
        boolean isSeller = market.getUser().getId().equals(userId);
        String transactionStatus = getTransactionStatus(market, isSeller);

        return MarketHistoryResponseDto.builder()
            .marketId(market.getId())
            .itemId(market.getItem().getId())
            .itemName(market.getItem().getName())
            .amount(trade.getAmount())
            .totalPrice(trade.getTotalPrice())
            .userRole(isSeller ? "SELLER" : "BUYER")
            .transactionStatus(transactionStatus)
            .transactionDate(trade.getCreatedAt())
            .build();
    }

    private static String getTransactionStatus(Market market, boolean isSeller) {
        if (market.getStatus() == Status.COMPLETED) {
            return isSeller ? "판매 완료" : "구매 완료";
        } else {
            return "판매 중";
        }
    }
}
