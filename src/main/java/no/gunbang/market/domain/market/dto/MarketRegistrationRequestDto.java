package no.gunbang.market.domain.market.dto;

import lombok.Getter;

@Getter
public class MarketRegistrationRequestDto {

    private Long itemId;
    private long price;
    private int amount;
}
