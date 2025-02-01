package no.gunbang.market.domain.market.repository;

import static no.gunbang.market.domain.market.entity.QMarket.market;
import static no.gunbang.market.domain.market.entity.QMarketTrade.marketTrade;
import static no.gunbang.market.domain.market.entity.QTrade.trade;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
}
