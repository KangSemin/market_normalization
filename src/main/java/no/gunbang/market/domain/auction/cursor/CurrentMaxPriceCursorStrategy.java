package no.gunbang.market.domain.auction.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.domain.auction.entity.QAuction;
import no.gunbang.market.domain.auction.entity.QBid;

public class CurrentMaxPriceCursorStrategy implements CursorStrategy<AuctionCursorValues> {

    @Override
    public Predicate buildCursorPredicate(Order order, Long lastAuctionId, AuctionCursorValues marketCursorValues) {
        Long lastCurrentMaxPrice = marketCursorValues.lastCurrentMaxPrice();
        if (lastCurrentMaxPrice == null || lastAuctionId == null) {
            long maxPrice = Long.MAX_VALUE;
            long maxId = Long.MAX_VALUE;
            return Expressions.booleanTemplate(
                "({0} < {1}) or ({0} = {1} and {2} < {3})",
                QBid.bid.bidPrice.coalesce(0L), maxPrice, QAuction.auction.id, maxId
            );
        }

        if (Order.DESC.equals(order)) {
            return Expressions.booleanTemplate(
                    "({0} < {1}) or ({0} = {1} and {2} < {3})",
                    QBid.bid.bidPrice.coalesce(0L), lastCurrentMaxPrice, QAuction.auction.id, lastAuctionId
            );
        } else {
            return Expressions.booleanTemplate(
                    "({0} > {1}) or ({0} = {1} and {2} > {3})",
                    QBid.bid.bidPrice.coalesce(0L), lastCurrentMaxPrice, QAuction.auction.id, lastAuctionId
            );
        }
    }
}
