package no.gunbang.market.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MarketSearchRequestDto {
    private String searchKeyword;
    private String sortBy;  // 정렬 기준 (e.g., price, amount, itemName)
    private String sortDirection;   //정렬방향 (ASC or DESC)
}
