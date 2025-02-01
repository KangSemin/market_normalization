package no.gunbang.market.common;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.gunbang.market.domain.user.entity.User;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor
public class Inventory {

    @Comment("인벤토리 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("아이템 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Comment("사용자 외래키")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Comment("사용자 인벤토리 아이템 개수")
    private int amount;

    public Inventory(Item item, User user, int amount) {
        this.item = item;
        this.user = user;
        this.amount = amount;
    }

    public void validateAmount(int amount) {
        if (this.amount < amount) {
            throw new RuntimeException("수량 부족");
        }
    }
}