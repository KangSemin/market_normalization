package no.gunbang.market.domain.market.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.user.dto.UserResponseDto;
import no.gunbang.market.domain.user.entity.User;

@Builder
@Getter
@AllArgsConstructor
public class MarketResponseDto {

    private Long id;
    private int amount;
    private long price;
    private Status status;
    private UserResponseDto user;
    private Item item;

    public static MarketResponseDto toDto(Market market) {
        return MarketResponseDto.builder()
            .id(market.getId())
            .amount(market.getAmount())
            .price(market.getPrice())
            .status(market.getStatus())
            .user(UserResponseDto.toDto(market.getUser()))
            .item(market.getItem())
            .build();
    }
}
