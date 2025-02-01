package no.gunbang.market.domain.market.repository;

import static no.gunbang.market.common.QItem.item;
import static no.gunbang.market.domain.market.entity.QMarket.market;
import static no.gunbang.market.domain.user.entity.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.dto.QMarketHistoryResponseDto;
import no.gunbang.market.domain.market.dto.QMarketListResponseDto;
import no.gunbang.market.domain.market.dto.QMarketResponseDto;
import no.gunbang.market.domain.market.entity.QMarket;
import no.gunbang.market.domain.market.entity.QTrade;
import no.gunbang.market.domain.user.dto.QUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
            .orderBy(market.id.asc(), trade.createdAt.coalesce(market.createdAt).desc())
            .fetch();

    }

    @Override
    public Page<MarketListResponseDto> findPopularTradeItems(LocalDateTime startDate, Pageable pageable) {
        QTrade trade = QTrade.trade;
        QMarket market = QMarket.market;

        JPQLQuery<MarketListResponseDto> query = queryFactory
            .select(new QMarketListResponseDto(
                market.item.id,
                market.item.name,
                trade.amount.sum().intValue(),
                trade.totalPrice.min(),
                trade.id.count()
            ))
            .from(trade)
            .leftJoin(trade.market, market)
            .where(trade.createdAt.goe(startDate))
            .groupBy(market.item.id, market.item.name)
            .orderBy(trade.id.count().desc())
            .limit(100);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public Page<MarketResponseDto> findAllMarkets(String name, Pageable pageable) {


        // name 동적 쿼리
        BooleanBuilder conditions = new BooleanBuilder();
        conditions.and(market.status.eq(Status.ON_SALE));
        if (name != null && !name.trim().isEmpty()) {
            conditions.and(market.item.name.contains(name));
        }

        // 최소 가격 구하는 서브 쿼리 추가
        QMarket marketSub = new QMarket("marketSub");
        JPQLQuery<Long> minPriceQuery = JPAExpressgions
            .select(marketSub.price.min())
            .from(marketSub)
            .where(
                marketSub.item.id.eq(market.item.id)
                    .and(marketSub.status.eq(Status.ON_SALE))
            );

        // 각 아이템 별로 최저가 MarketResponseDto 생성
        List<MarketResponseDto> markets = queryFactory
            .select(new QMarketResponseDto(
                market.id,
                market.amount,
                market.price,
                market.status,
                new QUserResponseDto(
                    user.nickname,
                    user.server,
                    user.level,
                    user.job,
                    user.gold
                ),
                market.item.id,
                market.item.name
            ))
            .from(market)
            .join(market.user, user)
            .join(market.item, item)
            .where(conditions.and(market.price.eq(minPriceQuery)))
            .orderBy(market.item.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(market.item.id.countDistinct())
            .from(market)
            .where(conditions.and(market.price.eq(minPriceQuery)))
            .fetchOne();

        return new PageImpl<>(markets, pageable, total == null ? 0 : total);
    }
}
