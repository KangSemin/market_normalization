package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.common.QItem;
import no.gunbang.market.domain.market.entity.QMarket;

public class PriceCursorStrategy implements CursorStrategy<MarketCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, MarketCursorValues marketCursorValues) {
        Long lastPrice = marketCursorValues.lastPrice();
        if (lastPrice == null || lastItemId == null) {
            long maxPrice = Long.MAX_VALUE;
            long maxItemId = Long.MAX_VALUE;
            return Expressions.booleanTemplate(
                "(MIN({0}) < {1}) OR (MIN({0}) = {1} AND {2} < {3})",
                QMarket.market.price, maxPrice, QItem.item.id, maxItemId
            );
        }

        if (Order.DESC.equals(order)) {
            return Expressions.booleanTemplate(
                    "(MIN({0}) < {1}) OR (MIN({0}) = {1} AND {2} < {3})",
                    QMarket.market.price, lastPrice, QItem.item.id, lastItemId
            );
        } else {
            return Expressions.booleanTemplate(
                    "(MIN({0}) > {1}) OR (MIN({0}) = {1} AND {2} > {3})",
                    QMarket.market.price, lastPrice, QItem.item.id, lastItemId
            );
        }
    }
}
