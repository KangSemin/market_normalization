package no.gunbang.market.domain.auction.repository;

import static no.gunbang.market.domain.auction.entity.QAuction.auction;
import static no.gunbang.market.domain.auction.entity.QBid.bid;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.entity.Auction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Auction> findUserAuctionHistory(Long userId) {
        return queryFactory
            .selectFrom(auction)
            .leftJoin(bid).on(auction.id.eq(bid.auction.id))
            .where(auction.user.id.eq(userId).or(bid.user.id.eq(userId)))
            .distinct()
            .fetch();
    }
}
