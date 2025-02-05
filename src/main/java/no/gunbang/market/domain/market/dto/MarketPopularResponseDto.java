package no.gunbang.market.domain.market.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MarketPopularResponseDto {

    private Long itemId;
    private String itemName;
    private int totalAmount;
    private long minPrice;
    private int tradeCount;

    @QueryProjection
    public MarketPopularResponseDto(Long itemId, String itemName, int totalAmount, long minPrice, int tradeCount) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.totalAmount = totalAmount;
        this.minPrice = minPrice;
        this.tradeCount = tradeCount;
    }
}
