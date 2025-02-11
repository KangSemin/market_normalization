package no.gunbang.market.domain.auction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.entity.BaseEntity;
import no.gunbang.market.common.entity.Item;
import no.gunbang.market.common.entity.Status;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.user.entity.User;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "auction")
@Getter
@NoArgsConstructor
public class Auction extends BaseEntity {

    @Comment("경매장 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Comment("경매 시작가")
    private long startingPrice;

    @Comment("경매 마감 기한")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Comment("경매 진행 상태")
    private Status status;

    @Comment("경매 참여자 수")
    private int bidderCount = 0;

    @Comment("사용자 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Comment("아이템 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private static final int minAuctionDays = 3;
    private static final int maxAuctionDays = 7;

    public static Auction of(
        User user,
        Item item,
        long startingPrice,
        int auctionDays
    ) {
        validateAuctionDays(auctionDays);

        Auction auction = new Auction();
        auction.user = user;
        auction.item = item;
        auction.startingPrice = startingPrice;
        auction.status = Status.ON_SALE;
        auction.dueDate = auction.toDueDate(auctionDays);
        return auction;
    }

    // 로그인한 사용자와 경매 등록한 사용자가 동일한지 검증
    public void validateUser(Long userId) {
        if (!Objects.equals(this.user.getId(), userId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
    }

    // 경매 취소
    public void delete() {
        this.status = Status.CANCELLED;
    }

    // 입찰자 수 증가
    public void incrementBidderCount() {
        this.bidderCount++;
    }

    // 만료된 경매 상태를 종료(COMPLETED)로 변경
    public void makeExpiredAuctionCompleted() {
        if (isAuctionExpired()) {
            this.status = Status.COMPLETED;
        }
    }

    // 경매가 만료되었는지 검증
    private boolean isAuctionExpired() {
        if (LocalDateTime.now().isAfter(this.getDueDate())) {
            return true;
        }
        return false;
    }

    // 경매 마감 기한을 계산하여 LocalDateTime으로 반환
    private LocalDateTime toDueDate(int auctionDays) {
        return LocalDateTime.now().plusDays(auctionDays);
    }

    // 경매 진행 기간이 최소 및 최대 제한을 만족하는지 검증
    private static void validateAuctionDays(int auctionDays) {
        if (auctionDays < minAuctionDays || auctionDays > maxAuctionDays) {
            throw new CustomException(ErrorCode.AUCTION_DAYS_OUT_OF_RANGE);
        }
    }
}