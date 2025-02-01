package no.gunbang.market.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.market.entity.Market;

@Builder
@Getter
@AllArgsConstructor
public class MarketHistoryResponseDto {

    private Long marketId;
    private Long itemId;
    private String itemName;
    private int amount;
    private long totalPrice;
    private String userRole;
    private String transactionStatus;

    public static MarketHistoryResponseDto toDto(Trade trade, Long userId) {
        Market market = trade.getMarket();

        boolean isSeller = market.getUser().getId().equals(userId); //true == 판매자
        String transactionStatus = getTransactionStatus(market, isSeller);

        return MarketHistoryResponseDto.builder()
            .marketId(market.getId())
            .itemId(market.getItem().getId())
            .itemName(market.getItem().getName())
            .amount(trade.getAmount())
            .totalPrice(trade.getTotalPrice())
            .userRole(isSeller ? "SELLER" : "BUYER")
            .transactionStatus(isSeller ? transactionStatus : "구매 완료")
            .build();
    }

    private static String getTransactionStatus(Market market, boolean isSeller) {
        if (market.getStatus() == Status.COMPLETED) {
            return isSeller ? "판매 완료" : "구매 완료";
        } else {
            return "판매 중";
        }
    }

    //TODO: 우진님이 market entity 수정하면 변경사항 생길 수 있음.
}
