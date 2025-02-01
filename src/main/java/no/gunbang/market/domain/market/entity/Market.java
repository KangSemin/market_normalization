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
import lombok.Getter;
import no.gunbang.market.common.BaseEntity;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.user.entity.User;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "market")
@Getter
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
}
