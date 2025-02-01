package no.gunbang.market.domain.auction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuctionSearchRequestDto {
    private String searchKeyword;
    private String sortBy;  //정렬기준 (e.g., itemName, startPrice, dueDate, currentPrice)
    private String sortDirection;   //정렬방향 (ASC or DESC)
}
