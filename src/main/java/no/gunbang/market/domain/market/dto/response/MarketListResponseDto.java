package no.gunbang.market.domain.market.dto.response;

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

    @QueryProjection
    public MarketListResponseDto(Long itemId, String itemName, int totalAmount, long minPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.totalAmount = totalAmount;
        this.minPrice = minPrice;
    }
}