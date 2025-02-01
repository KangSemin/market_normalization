package no.gunbang.market.domain.market.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.user.entity.User;

@Builder
@Getter
@AllArgsConstructor
public class MarketResponseDto {

    private Long id;
    private int amount;
    private long price;
    private Status status;
    private User user;
    private Item item;
}
