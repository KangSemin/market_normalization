package no.gunbang.market.domain.auction.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import no.gunbang.market.common.query.CursorStrategy;
import no.gunbang.market.domain.auction.entity.QAuction;

public class AuctionDefaultCursorStrategy implements CursorStrategy<AuctionCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastAuctionId, AuctionCursorValues auctionCursorValues) {
        if (Order.DESC.equals(order)) {
            return QAuction.auction.id.lt(lastAuctionId);
        } else {
            return QAuction.auction.id.gt(lastAuctionId);
        }
    }
}
