package no.gunbang.market.domain.market.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
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
            .select(Projections.fields(MarketHistoryResponseDto.class,
                market.id.as("marketId"),
                market.item.id.as("itemId"),
                market.item.name.as("itemName"),
                trade.amount.coalesce(market.amount).as("amount"),
                trade.totalPrice.coalesce(market.price).as("totalPrice"),
                trade.createdAt.coalesce(market.createdAt).as("transactionDate"),

                new CaseBuilder()
                    .when(market.user.id.eq(userId)).then("SELLER")
                    .otherwise("BUYER")
                    .as("userRole"),

                new CaseBuilder()
                    .when(market.status.eq(Status.COMPLETED).and(market.user.id.eq(userId))).then("판매완료")
                    .when(market.status.eq(Status.ON_SALE).and(market.user.id.eq(userId))).then("판매중")
                    .otherwise("구매완료")
                    .as("transactionStatus")
            ))
            .from(market)
            .leftJoin(trade).on(market.id.eq(trade.market.id))
            .where(
                market.user.id.eq(userId)
                    .or(trade.user.id.eq(userId))
            )
            .orderBy(market.id.asc(), trade.createdAt.coalesce(market.createdAt).desc())
            .fetch();
    }

    @Override
    public Page<MarketListResponseDto> findPopularTradeItems(LocalDateTime startDate, Pageable pageable) {
        QTrade trade = QTrade.trade;
        QMarket market = QMarket.market;

        JPQLQuery<MarketListResponseDto> query = queryFactory
            .select(Projections.fields(MarketListResponseDto.class,
                market.item.id.as("itemId"),
                market.item.name.as("itemName"),
                trade.amount.sum().intValue().as("totalAmount"),
                trade.totalPrice.min().as("minPrice"),
                trade.id.count().as("tradeCount")
            ))
            .from(trade)
            .leftJoin(trade.market, market)
            .where(trade.createdAt.goe(startDate))
            .groupBy(market.item.id, market.item.name)
            .orderBy(trade.id.count().desc())
            .limit(100);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }
}
