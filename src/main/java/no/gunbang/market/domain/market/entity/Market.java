package no.gunbang.market.domain.market.entity;

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
@Table(name = "market")
@Getter
@NoArgsConstructor
public class Market extends BaseEntity {

    @Comment("거래소 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Comment("아이템 수량")
    private int amount;

    @Comment("아이템 가격")
    private long price;

    @Enumerated(EnumType.STRING)
    @Comment("아이템 거래 상태")
    private Status status;

    @Comment("사용자 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Comment("아이템 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static Market of(int amount, long price, Status status, User user, Item item) {
        Market market = new Market();
        market.amount = amount;
        market.price = price;
        market.status = status;
        market.user = user;
        market.item = item;
        return market;
    }

    public void decreaseAmount(int buyAmount) {
        amount -= buyAmount;

        if (amount == 0) {
            status = Status.COMPLETED;
        }
    }

    public void validateUser(Long userId) {
        if (!Objects.equals(this.user.getId(), userId)) {
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }
    }

    public void delete() {
        this.status = Status.CANCELLED;
    }
}
