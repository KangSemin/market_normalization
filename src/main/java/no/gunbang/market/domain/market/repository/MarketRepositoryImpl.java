package no.gunbang.market.domain.market.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.CursorStrategy;
import no.gunbang.market.common.QItem;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.cursor.*;
import no.gunbang.market.domain.market.dto.*;
import no.gunbang.market.domain.market.entity.QMarket;
import no.gunbang.market.domain.market.entity.QTrade;
import no.gunbang.market.domain.market.entity.QTradeCount;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MarketRepositoryImpl implements MarketRepositoryCustom {

    private static final int PAGE_SIZE = 10;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TradeHistoryResponseDto> findUserTradeHistory(Long userId) {
        QTrade trade = QTrade.trade;
        QMarket market = QMarket.market;

        return queryFactory
            .select(new QTradeHistoryResponseDto(
                trade.id,
                market.item.id,
                market.item.name,
                trade.amount,
                trade.totalPrice,
                trade.createdAt
            ))
            .from(trade)
            .join(trade.market, market)
            .where(trade.user.id.eq(userId))
            .fetch();
    }

    @Override
    public List<MarketHistoryResponseDto> findUserMarketHistory(Long userId) {
        QMarket market = QMarket.market;

        return queryFactory
            .select(new QMarketHistoryResponseDto(
                market.id,
                market.item.id,
                market.item.name,
                market.amount,
                market.price,
                market.status,
                market.createdAt
            ))
            .from(market)
            .where(market.user.id.eq(userId))
            .fetch();
    }

    @Override
    public List<MarketPopularResponseDto> findPopularMarketItems(
        LocalDateTime startDate,
        Long lastTradeCount,
        Long lastItemId
    ) {
        QMarket market = QMarket.market;
        QTradeCount tradeCount = QTradeCount.tradeCount;

        BooleanBuilder builder = new BooleanBuilder();
        builder
            .and(market.status.eq(Status.ON_SALE))
            .and(market.createdAt.goe(startDate));

        if (lastTradeCount != null) {
            builder.and(
                Expressions.booleanTemplate(
                    "({0} < {1}) OR ({0} = {1} AND {2} < {3})",
                        tradeCount.count, lastTradeCount, market.item.id, lastItemId
                )
            );
        } else {
            int maxTradeCount = Integer.MAX_VALUE;
            long maxItemId = Long.MAX_VALUE;
            builder.and(
                Expressions.booleanTemplate(
                    "({0} < {1}) OR ({0} = {1} AND {2} < {3})",
                    tradeCount.count, maxTradeCount, market.item.id, maxItemId
                )
            );
        }

        return queryFactory
            .select(new QMarketPopularResponseDto(
                market.item.id,
                market.item.name,
                market.amount.sum().coalesce(0),
                market.price.min().coalesce(0L),
                tradeCount.count.intValue()
            ))
            .from(market)
            .leftJoin(tradeCount).on(market.item.id.eq(tradeCount.itemId))
            .where(builder)
            .groupBy(market.item.id, market.item.name)
            .orderBy(tradeCount.count.desc(), market.item.id.desc())
            .limit(PAGE_SIZE)
            .fetch();
    }

    @Override
    public List<MarketListResponseDto> findAllMarketItems(
        String searchKeyword,
        String sortBy,
        String sortDirection,
        Long lastItemId,
        MarketCursorValues marketCursorValues
    ) {
        QMarket market = QMarket.market;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            builder.and(item.name.containsIgnoreCase(searchKeyword));
        }
        builder.and(market.status.eq(Status.ON_SALE));

        Order order = "DESC".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        Predicate havingClause = null;
        if(lastItemId != null) {
            havingClause = getCursorStrategy(sortBy).buildCursorPredicate(order, lastItemId, marketCursorValues);
        }
        

        return queryFactory
            .select(new QMarketListResponseDto(
                item.id,
                item.name,
                market.amount.sum().coalesce(0),
                market.price.min().coalesce(0L)
            ))
            .from(market)
            .join(market.item, item)
            .where(builder)
            .groupBy(item.id, item.name)
            .having(havingClause)
            .orderBy(determineSorting(order, sortBy))
            .limit(PAGE_SIZE)
            .fetch();
    }

    private CursorStrategy<MarketCursorValues> getCursorStrategy(String sortBy) {
        return switch (sortBy) {
            case "price" -> new PriceCursorStrategy();
            case "amount" -> new AmountCursorStrategy();
            default -> new MarketDefaultCursorStrategy();
        };
    }

    private OrderSpecifier<?> determineSorting(Order order, String sortBy) {
        return switch (sortBy) {
            case "price" -> new OrderSpecifier<>(order, QMarket.market.price.min());
            case "amount" -> new OrderSpecifier<>(order, QMarket.market.amount.sum());
            default -> new OrderSpecifier<>(order, QItem.item.id);
        };
    }
}
