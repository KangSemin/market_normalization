package no.gunbang.market.domain.market.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketListResponseDto {

    private Long itemId;
    private String itemName;
    private int totalAmount;
    private long minPrice;
    private long tradeCount;

    @QueryProjection
    public MarketListResponseDto(Long itemId, String itemName, int totalAmount, long minPrice, long tradeCount) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.totalAmount = totalAmount;
        this.minPrice = minPrice;
        this.tradeCount = tradeCount;
    }
}
