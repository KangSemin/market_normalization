package no.gunbang.market.domain.market.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.user.dto.UserResponseDto;

@Builder
@Getter
public class MarketResponseDto {

    private Long id;
    private int amount;
    private long price;
    private Status status;
    private String userName;
    private Long itemId;
    private String itemName;

    @QueryProjection
    public MarketResponseDto(
        Long id,
        int amount,
        long price,
        Status status,
        String userName,
        Long itemId,
        String itemName)
    {
        this.id = id;
        this.amount = amount;
        this.price = price;
        this.status = status;
        this.userName = userName;
        this.itemId = itemId;
        this.itemName = itemName;
    }


    public static MarketResponseDto toDto(Market market) {
        return MarketResponseDto.builder()
            .id(market.getId())
            .amount(market.getAmount())
            .price(market.getPrice())
            .status(market.getStatus())
            .userName((market.getUser().getNickname()))
            .itemId(market.getItem().getId())
            .itemName(market.getItem().getName())
            .build();
    }
}
