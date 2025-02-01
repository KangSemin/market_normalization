package no.gunbang.market.domain.market.repository;

import static no.gunbang.market.common.QItem.item;
import static no.gunbang.market.domain.market.entity.QMarket.market;
import static no.gunbang.market.domain.market.entity.QTrade.trade;
import static no.gunbang.market.domain.user.entity.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.dto.MarketResponseDto;
import no.gunbang.market.domain.market.entity.QMarket;
import no.gunbang.market.domain.market.entity.QTrade;
import no.gunbang.market.domain.market.entity.Trade;
import no.gunbang.market.domain.user.dto.UserResponseDto;
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
    public List<Trade> findUserMarketHistory(Long userId) {
        return queryFactory
            .selectFrom(trade)
            .leftJoin(trade.market, market).fetchJoin()
            .where(market.isNotNull()
                .and(market.user.id.eq(userId).or(trade.user.id.eq(userId))))
            .distinct()
            .fetch();
    }
    //TODO: history 조회쪽도 queryDSL에서 바로 dto로 반환하도록 변경해야 함

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

    @Override
    public Page<MarketResponseDto> findAllMarkets(String name, Pageable pageable) {

        BooleanBuilder conditions = new BooleanBuilder();
        conditions.and(market.status.eq(Status.ON_SALE));
        if (name != null && !name.trim().isEmpty()) {
            conditions.and(market.item.name.contains(name));
        }

        // 각 아이템 별로 최저가 MarketResponseDto 생성
        List<MarketResponseDto> markets = queryFactory
            .select(Projections.constructor(
                MarketResponseDto.class,
                market.id,
                market.amount,
                market.price.min(),
                market.status,
                Projections.constructor(UserResponseDto.class,
                    user.id, user.nickname, user.gold),
                market.item
            ))
            .from(market)
            .join(market.user, user)
            .join(market.item, item)
            .where(conditions)
            .groupBy(market.item.name)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(markets, pageable, getMarketsTotalCount(name));
    }

    // 페이징에 필요한 총 개수
    private Long getMarketsTotalCount(String name) {

        BooleanBuilder conditions = new BooleanBuilder();
        conditions.and(market.status.eq(Status.ON_SALE));

        if (name != null && !name.trim().isEmpty()) {
            conditions.and(market.item.name.contains(name));
        }

        return queryFactory
            .select(market.item.name.countDistinct())
            .from(market)
            .where(conditions)
            .fetchOne();
    }
}
