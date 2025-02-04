package no.gunbang.market.domain.market.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.QItem;
import no.gunbang.market.common.QTradeCount;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.dto.*;
import no.gunbang.market.domain.market.entity.QMarket;
import no.gunbang.market.domain.market.entity.QTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MarketRepositoryImpl implements MarketRepositoryCustom {

    private static final int POPULAR_LIMIT = 200;

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
    public Page<MarketPopularResponseDto> findPopularMarketItems(LocalDateTime startDate, Pageable pageable) {
        QMarket market = QMarket.market;
        QTradeCount tradeCount = QTradeCount.tradeCount;
        JPQLQuery<MarketPopularResponseDto> query = queryFactory
                .select(new QMarketPopularResponseDto(
                        market.item.id,
                        market.item.name,
                        market.amount.sum().coalesce(0),
                        market.price.min().coalesce(0L),
                        tradeCount.count()
                ))
                .from(market)
                .leftJoin(tradeCount).on(market.item.id.eq(tradeCount.itemId))
                .where(market.status.eq(Status.ON_SALE)
                        .and(market.createdAt.goe(startDate))
                )
                .groupBy(market.id, market.item.id, market.item.name, tradeCount.count)
                .orderBy(tradeCount.count.desc())
                .limit(POPULAR_LIMIT);
        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    public Page<MarketListResponseDto> findAllMarketItems(
        String searchKeyword,
        String sortBy,
        String sortDirection,
        Pageable pageable
    ) {
        QMarket market = QMarket.market;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            builder.and(item.name.containsIgnoreCase(searchKeyword));
        }
        builder
            .and(market.status.eq(Status.ON_SALE));

        JPQLQuery<MarketListResponseDto> query = queryFactory
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
            .orderBy(determineSorting(sortBy, sortDirection))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        List<MarketListResponseDto> content = query.fetch();

        Long count = queryFactory
            .select(item.id.countDistinct())
            .from(market)
            .leftJoin(market.item, item)
            .where(builder)
            .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count == null ? 0 : count);
    }

    private OrderSpecifier<?> determineSorting(String sortBy, String sortDirection) {
        Order order = "DESC".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        return switch (sortBy) {
            case "price" -> new OrderSpecifier<>(order, QMarket.market.price.min());
            case "amount" -> new OrderSpecifier<>(order, QMarket.market.amount.sum());
            case "itemName" -> new OrderSpecifier<>(order, QItem.item.name);
            default -> new OrderSpecifier<>(order, Expressions.numberTemplate(Double.class, "rand()"));
        };
    }
}
