package no.gunbang.market.domain.market.dto.request;

import lombok.Getter;

@Getter
public class MarketTradeRequestDto {

    private Long itemId;
    private int amount;
}
