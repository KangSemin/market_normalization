package no.gunbang.market.domain.auction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.BaseEntity;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.user.entity.User;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(name = "bid")
@NoArgsConstructor
public class Bid extends BaseEntity {

    @Comment("입찰 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Comment("입찰 가격")
    private long bidPrice;

    @Comment("마지막 입찰 성공 시간")
    private LocalDateTime updatedAt;

    @Comment("경매 외래키")
    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Comment("사용자 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Bid of(
        User user,
        Auction auction,
        long bidPrice
    ) {
        validateNewBid(auction, bidPrice);

        Bid bid = new Bid();
        bid.user = user;
        bid.auction = auction;
        bid.bidPrice = bidPrice;
        return bid;
    }

    public void updateBid(
        long bidPrice,
        User user
    ) {
        validateBidUpdate(bidPrice);

        this.bidPrice = bidPrice;
        this.user = user;
        this.updatedAt = LocalDateTime.now();
    }

    // 새로운 입찰 생성 시 최소 입찰 가격 검증
    private static void validateNewBid(
        Auction auction,
        long bidPrice
    ) {
        if (bidPrice < auction.getStartingPrice()) {
            throw new CustomException(ErrorCode.LACK_OF_GOLD);
        }
    }

    // 기존 입찰이 있을 때, 기존의 입찰 가격보다 같거나 낮은지 검증
    private void validateBidUpdate(long newBidPrice) {
        if (newBidPrice <= this.bidPrice) {
            throw new CustomException(ErrorCode.BID_TOO_LOW);
        }
    }
}