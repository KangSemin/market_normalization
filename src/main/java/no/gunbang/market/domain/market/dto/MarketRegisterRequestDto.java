package no.gunbang.market.domain.market.dto;

import lombok.Getter;

@Getter
public class MarketRegisterRequestDto {

    private Long itemId;
    private long price;
    private int amount;
}
