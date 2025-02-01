package no.gunbang.market.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MarketListResponseDto {

    private Long itemId;
    private String itemName;
    private int totalAmount;
    private long minPrice;
    private long tradeCount;
}
