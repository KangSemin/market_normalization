package no.gunbang.market.domain.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.domain.market.entity.Trade;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketTradeResponseDto {

    private Long id;
    private Long userId;
    private Long marketId;
    private int amount;
    private long totalPrice;

    public static MarketTradeResponseDto toDto(Trade trade) {
        return MarketTradeResponseDto.builder()
            .id(trade.getId())
            .userId(trade.getUser().getId())
            .marketId(trade.getMarket().getId())
            .amount(trade.getAmount())
            .totalPrice(trade.getTotalPrice())
            .build();
    }
}
