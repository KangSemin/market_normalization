package no.gunbang.market.domain.market.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.QItem;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.QMarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.QMarketListResponseDto;
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
    public List<MarketHistoryResponseDto> findUserMarketHistory(Long userId) {
        QMarket market = QMarket.market;
        QTrade trade = QTrade.trade;

        return queryFactory
            .select(new QMarketHistoryResponseDto(
                market.id,
                market.item.id,
                market.item.name,
                trade.amount.coalesce(market.amount),
                trade.totalPrice.coalesce(market.price),
                new CaseBuilder()
                    .when(market.user.id.eq(userId)).then("SELLER")
                    .otherwise("BUYER"),
                new CaseBuilder()
                    .when(market.status.eq(Status.COMPLETED).and(market.user.id.eq(userId))).then("판매완료")
                    .when(market.status.eq(Status.ON_SALE).and(market.user.id.eq(userId))).then("판매중")
                    .otherwise("구매완료"),
                trade.createdAt.coalesce(market.createdAt)
            ))
            .from(market)
            .leftJoin(trade).on(market.id.eq(trade.market.id))
            .where(market.user.id.eq(userId).or(trade.user.id.eq(userId)))
            .where(market.status.ne(Status.CANCELLED))
            .orderBy(market.id.asc(), trade.createdAt.coalesce(market.createdAt).desc())
            .fetch();
    }

    @Override
    public Page<MarketListResponseDto> findPopularMarketItems(LocalDateTime startDate, Pageable pageable) {
        QTrade trade = QTrade.trade;
        QMarket market = QMarket.market;

        JPQLQuery<MarketListResponseDto> query = queryFactory
            .select(new QMarketListResponseDto(
                market.item.id,
                market.item.name,
                JPAExpressions
                    .select(market.amount.sum().coalesce(0))
                    .from(market)
                    .where(market.item.id.eq(trade.market.item.id)),
                market.price.min().coalesce(0L),
                JPAExpressions
                    .select(trade.id.count().coalesce(0L))
                    .from(trade)
                    .where(market.item.id.eq(trade.market.item.id))
            ))
            .from(trade)
            .leftJoin(trade.market, market)
            .where(trade.createdAt.goe(startDate)
                .and(market.status.ne(Status.COMPLETED))
                .and(market.status.ne(Status.CANCELLED))
            )
            .groupBy(market.id, market.item.id, market.item.name, market.amount, market.price)
            .orderBy(trade.id.count().desc())
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
        QTrade trade = QTrade.trade;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            builder.and(item.name.containsIgnoreCase(searchKeyword));
        }
        builder
            .and(market.status.ne(Status.COMPLETED))
            .and(market.status.ne(Status.CANCELLED));

        JPQLQuery<MarketListResponseDto> query = queryFactory
            .select(new QMarketListResponseDto(
                item.id,
                item.name,
                market.amount.sum().coalesce(0),
                market.price.min().coalesce(0L),
                JPAExpressions
                    .select(trade.id.countDistinct())
                    .from(trade)
                    .where(trade.market.item.id.eq(item.id))
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
            case "random" -> new OrderSpecifier<>(order, Expressions.numberTemplate(Double.class, "rand()"));
            default -> new OrderSpecifier<>(order, QItem.item.name);
        };
    }
}
