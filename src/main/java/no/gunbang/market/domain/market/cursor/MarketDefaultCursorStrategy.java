package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import no.gunbang.market.common.query.CursorStrategy;
import no.gunbang.market.domain.market.entity.QMarket;

public class MarketDefaultCursorStrategy implements CursorStrategy<MarketCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, MarketCursorValues marketCursorValues) {
        if (Order.DESC.equals(order)) {
            return QMarket.market.item.id.lt(lastItemId);
        } else {
            return QMarket.market.item.id.gt(lastItemId);
        }
    }
}

