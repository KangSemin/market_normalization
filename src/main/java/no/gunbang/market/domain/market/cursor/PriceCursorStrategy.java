package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.query.CursorStrategy;
import no.gunbang.market.domain.market.entity.QMarket;

public class PriceCursorStrategy implements CursorStrategy<MarketCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, MarketCursorValues marketCursorValues) {
        Long lastPrice = marketCursorValues.lastPrice();

        if (Order.DESC.equals(order)) {
            return Expressions.booleanTemplate(
                    "(MIN({0}) < {1}) OR (MIN({0}) = {1} AND {2} < {3})",
                    QMarket.market.price, lastPrice, QMarket.market.item.id, lastItemId
            );
        } else {
            return Expressions.booleanTemplate(
                    "(MIN({0}) > {1}) OR (MIN({0}) = {1} AND {2} > {3})",
                    QMarket.market.price, lastPrice, QMarket.market.item.id, lastItemId
            );
        }
    }
}
