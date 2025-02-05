package no.gunbang.market.domain.market.dto;

import lombok.Getter;

@Getter
public class MarketTradeRequestDto {

    private Long itemId;
    private int amount;
}
