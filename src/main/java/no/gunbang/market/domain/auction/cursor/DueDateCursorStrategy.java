package no.gunbang.market.domain.auction.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.domain.auction.entity.QAuction;

import java.time.LocalDateTime;

public class DueDateCursorStrategy implements CursorStrategy<AuctionCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastAuctionId, AuctionCursorValues marketCursorValues) {
        LocalDateTime lastDueDate = marketCursorValues.lastDueDate();
        if (lastDueDate == null || lastAuctionId == null) {
            LocalDateTime maxDueDate = LocalDateTime.MAX;
            long maxId = Long.MAX_VALUE;
            return Expressions.booleanTemplate(
                "({0} < {1}) or ({0} = {1} and {2} < {3})",
                QAuction.auction.dueDate, maxDueDate, QAuction.auction.id, maxId
            );
        }

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
