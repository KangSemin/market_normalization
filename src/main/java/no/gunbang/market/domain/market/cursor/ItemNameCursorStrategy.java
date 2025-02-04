package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.QItem;

public class ItemNameCursorStrategy implements CursorStrategy {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastItemId, CursorValues cursorValues) {
        String lastItemName = cursorValues.lastItemName();
        if (Order.DESC.equals(order)) {
            return Expressions.booleanTemplate(
                    "({0} < {1}) or ({0} = {1} and {2} < {3})",
                    QItem.item.name, lastItemName, QItem.item.id, lastItemId
            );
        } else {
            return Expressions.booleanTemplate(
                    "({0} > {1}) or ({0} = {1} and {2} > {3})",
                    QItem.item.name, lastItemName, QItem.item.id, lastItemId
            );
        }
    }
}

