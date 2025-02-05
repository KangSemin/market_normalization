package no.gunbang.market.domain.auction.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.domain.auction.entity.QAuction;

public class AuctionDefaultCursorStrategy implements CursorStrategy<AuctionCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastAuctionId, AuctionCursorValues auctionCursorValues) {
        if (lastAuctionId == null) {
            long maxId = Long.MAX_VALUE;
            return Expressions.booleanTemplate("{0} < {1}", QAuction.auction.id, maxId);
        }
        if (Order.DESC.equals(order)) {
            return QAuction.auction.id.lt(lastAuctionId);
        } else {
            return QAuction.auction.id.gt(lastAuctionId);
        }
    }
}
