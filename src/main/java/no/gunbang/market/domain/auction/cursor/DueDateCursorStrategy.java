package no.gunbang.market.domain.auction.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import java.time.LocalDateTime;
import no.gunbang.market.common.query.CursorStrategy;
import no.gunbang.market.domain.auction.entity.QAuction;

public class DueDateCursorStrategy implements CursorStrategy<AuctionCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastAuctionId, AuctionCursorValues marketCursorValues) {
        LocalDateTime lastDueDate = marketCursorValues.lastDueDate();
        if (Order.DESC.equals(order)) {
            return Expressions.booleanTemplate(
                    "({0} < {1}) or ({0} = {1} and {2} < {3})",
                    QAuction.auction.dueDate, lastDueDate, QAuction.auction.id, lastAuctionId
            );
        } else {
            return Expressions.booleanTemplate(
                    "({0} > {1}) or ({0} = {1} and {2} > {3})",
                    QAuction.auction.dueDate, lastDueDate, QAuction.auction.id, lastAuctionId
            );
        }
    }
}
