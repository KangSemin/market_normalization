package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.common.QItem;
import no.gunbang.market.domain.market.entity.QMarket;

public class AmountCursorStrategy implements CursorStrategy<MarketCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, MarketCursorValues marketCursorValues) {
        Long lastAmount = marketCursorValues.lastAmount();
        if (Order.DESC.equals(order)) {
            return Expressions.booleanTemplate(
                    "({0} < {1}) or ({0} = {1} and {2} < {3})",
                    QMarket.market.amount.sum(), lastAmount, QItem.item.id, lastItemId
            );
        } else {
            return Expressions.booleanTemplate(
                    "({0} > {1}) or ({0} = {1} and {2} > {3})",
                    QMarket.market.amount.sum(), lastAmount, QItem.item.id, lastItemId
            );
        }
    }
    //TODO:500에러 나느중
}
