package no.gunbang.market.domain.market.repository;

import static no.gunbang.market.common.QItem.item;
import static no.gunbang.market.domain.market.entity.QMarket.market;
import static no.gunbang.market.domain.market.entity.QMarketTrade.marketTrade;
import static no.gunbang.market.domain.market.entity.QTrade.trade;
import static no.gunbang.market.domain.user.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.market.entity.Market;
import no.gunbang.market.domain.market.entity.MarketTrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MarketRepositoryImpl implements MarketRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MarketTrade> findUserMarketHistory(Long userId) {
        return queryFactory
            .selectFrom(marketTrade)
            .leftJoin(trade).on(marketTrade.trade.id.eq(trade.id))
            .leftJoin(market).on(marketTrade.market.id.eq(market.id))
            .where(market.user.id.eq(userId).or(trade.user.id.eq(userId)))
            .distinct()
            .fetch();
    }

    @Override
    public Page<Market> findAllMarkets(String name, Pageable pageable) {

        JPAQuery<Market> query = queryFactory.selectFrom(market)
            .join(market.user, user).fetchJoin()
            .join(market.item, item).fetchJoin();

        if (name != null && !name.trim().isEmpty()) {
            query.where(market.item.name.contains(name));
        }

        List<Market> markets = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(
            markets,
            pageable,
            getMarketsTotalCount(name)
            );
    }

    private Long getMarketsTotalCount(String name) {

        JPAQuery<Long> countQuery = queryFactory.select(market.count())
            .from(market);

        if (name != null && !name.trim().isEmpty()) {
            countQuery.where(market.item.name.contains(name));
        }

        return countQuery.fetchOne();
    }
}
