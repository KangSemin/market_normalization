package no.gunbang.market.domain.market.repository;

import static no.gunbang.market.domain.market.entity.QMarket.market;
import static no.gunbang.market.domain.market.entity.QTrade.trade;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.dto.MarketListResponseDto;
import no.gunbang.market.domain.market.entity.QMarket;
import no.gunbang.market.domain.market.entity.QTrade;
import no.gunbang.market.domain.market.entity.Trade;
import org.springframework.data.domain.Page;
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
            .orderBy(trade.id.count().desc());

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }
}
