package no.gunbang.market.common;

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

    private long count = 0;
}
