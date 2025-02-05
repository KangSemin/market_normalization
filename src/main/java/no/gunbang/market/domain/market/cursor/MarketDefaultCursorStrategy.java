package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.common.QItem;

public class MarketDefaultCursorStrategy implements CursorStrategy<MarketCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, MarketCursorValues marketCursorValues) {
        if (Order.DESC.equals(order)) {
            return QItem.item.id.lt(lastItemId);
        } else {
            return QItem.item.id.gt(lastItemId);
        }
    }
}

