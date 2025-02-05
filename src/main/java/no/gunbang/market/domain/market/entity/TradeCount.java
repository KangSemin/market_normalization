package no.gunbang.market.domain.market.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trade_count")
@NoArgsConstructor
@Getter
public class TradeCount {

    @Id
    private Long itemId;

    private int count = 0;

    public static TradeCount of(Long itemId, int count) {
        TradeCount tradeCount = new TradeCount();
        tradeCount.itemId = itemId;
        tradeCount.count = count;
        return tradeCount;
    }

    public synchronized void increaseCount(int additionalCount) {
        this.count += additionalCount;
    }
}
