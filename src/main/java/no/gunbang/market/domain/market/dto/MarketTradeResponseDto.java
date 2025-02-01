package no.gunbang.market.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.user.dto.UserResponseDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketTradeResponseDto {

    private Long id;
    private UserResponseDto user;
    private MarketResponseDto market;
    private int amount;
    private long totalPrice;

    public static MarketTradeResponseDto toDto(Trade trade) {
        return MarketTradeResponseDto.builder()
            .id(trade.getId())
            .user(UserResponseDto.toDto(trade.getUser()))
            .market(MarketResponseDto.toDto(trade.getMarket()))
            .amount(trade.getAmount())
            .totalPrice(trade.getTotalPrice())
            .build();
    }
}
