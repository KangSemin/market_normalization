package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.domain.market.entity.QMarket;

public class MarketDefaultCursorStrategy implements CursorStrategy<MarketCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, MarketCursorValues marketCursorValues) {
        if (lastItemId == null) {
            long maxItemId = Long.MAX_VALUE;
            return Expressions.booleanTemplate("{0} < {1}", QMarket.market.item.id, maxItemId);
        }
        if (Order.DESC.equals(order)) {
            return QMarket.market.item.id.lt(lastItemId);
        } else {
            return QMarket.market.item.id.gt(lastItemId);
        }
    }
}

