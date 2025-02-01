package no.gunbang.market.domain.market.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.market.entity.Market;

@Builder
@Getter
@AllArgsConstructor
public class MarketListResponseDto {

    private Long itemId;
    private String itemName;
    private int totalAmount;
    private long minPrice;
    private List<MarketItemDto> marketItems;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class MarketItemDto {
        private Long marketId;
        private int amount;
        private long price;
    }

    public static MarketListResponseDto toDto(Long itemId, String itemName, List<Market> markets) {
        List<MarketItemDto> marketItems = markets.stream()
            .map(market -> new MarketItemDto(market.getId(), market.getAmount(), market.getPrice()))
            .collect(Collectors.toList());

        int totalAmount = markets.stream().mapToInt(Market::getAmount).sum();
        long minPrice = markets.stream().mapToLong(Market::getPrice).min().orElse(0L);

        return MarketListResponseDto.builder()
            .itemId(itemId)
            .itemName(itemName)
            .totalAmount(totalAmount)
            .minPrice(minPrice)
            .marketItems(marketItems)
            .build();
    }
}
