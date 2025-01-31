package no.gunbang.market.domain.auction.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.entity.Auction;

@Builder
@Getter
@AllArgsConstructor
public class AuctionHistoryResponseDto {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private long startPrice;
    private long userBidPrice;
    private long currentMaxPrice;
    private String bidStatus;
    private String saleStatus;
    private boolean isSeller;
    private LocalDateTime dueDate;
    private Status status;

    public static AuctionHistoryResponseDto toDto(Auction auction, long currentMaxPrice, Long userBidPrice, boolean isWinningBid, boolean isSeller) {
        return AuctionHistoryResponseDto.builder()
            .auctionId(auction.getId())
            .itemId(auction.getItem().getId())
            .itemName(auction.getItem().getName())
            .startPrice(auction.getStartingPrice())
            .userBidPrice(userBidPrice != null ? userBidPrice : 0L)
            .currentMaxPrice(currentMaxPrice)
            .bidStatus(userBidPrice == null ? "사용자가 판매자임" : (isWinningBid ? "입찰 완료" : "입찰 실패"))
            .saleStatus(auction.getStatus() == Status.COMPLETED ? "판매 완료" : "판매 중")
            .isSeller(isSeller)
            .dueDate(auction.getDueDate())
            .status(auction.getStatus())
            .build();
    }
}
