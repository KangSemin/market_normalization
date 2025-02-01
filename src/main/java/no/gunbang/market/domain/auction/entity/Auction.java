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
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.BaseEntity;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.Status;
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

    @Comment("사용자 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Comment("아이템 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

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

    private LocalDateTime toDueDate(int auctionDays) {
        return LocalDateTime.now().plusDays(auctionDays);
    }

    private static void validateAuctionDays(int auctionDays) {
        if (auctionDays < 3 || auctionDays > 7) {
            throw new CustomException(ErrorCode.AUCTION_DAYS_OUT_OF_RANGE);
        }
    }
}