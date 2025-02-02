package no.gunbang.market.domain.market.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
            .limit(200);
        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public Page<MarketListResponseDto> findAllMarketItems(String searchKeyword, String sortBy, String sortDirection, Pageable pageable) {
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
                market.item.id,
                market.item.name,
                JPAExpressions
                    .select(market.amount.sum().coalesce(0))
                    .from(market)
                    .where(market.item.id.eq(item.id)),
                market.price.min().coalesce(0L),
//                JPAExpressions
//                    .select(market.price.min().coalesce(0L))
//                    .from(market)
//                    .where(market.item.id.eq(item.id)),
                JPAExpressions
                    .select(trade.id.count().coalesce(0L))
                    .from(trade)
                    .where(trade.market.item.id.eq(item.id))
            ))
            .from(market)
            .leftJoin(trade).on(market.id.eq(trade.market.id)).fetchJoin()
            .leftJoin(item).on(market.item.id.eq(item.id)).fetchJoin()
            .where(builder)
            .groupBy(market.id, market.item.id, market.item.name)
            .orderBy(determineSorting(sortBy, sortDirection, market, trade))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(query.fetch(), pageable, () -> {
            JPAQuery<Long> countQuery = queryFactory
                .select(market.item.id.countDistinct())
                .from(market)
                .leftJoin(trade).on(market.id.eq(trade.market.id))
                .leftJoin(item).on(market.item.id.eq(item.id))
                .where(builder);

            return Optional.ofNullable(countQuery.fetchOne()).orElse(0L);
        });
    }

    private OrderSpecifier<?> determineSorting(String sortBy, String sortDirection, QMarket market, QTrade trade) {
        Order order = "DESC".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        return switch (sortBy) {
            case "itemName" -> new OrderSpecifier<>(order, market.item.name);
            case "price" -> new OrderSpecifier<>(order, market.price.min());
            case "amount" -> new OrderSpecifier<>(
                order,
                JPAExpressions
                    .select(market.amount.sum().coalesce(0))
                    .from(market)
                    .where(market.item.id.eq(trade.market.item.id))
            );
            default -> new OrderSpecifier<>(Order.ASC, Expressions.numberTemplate(Long.class, "RAND()"));
        };
    }
}
