package no.gunbang.market.domain.auction.repository;

import static no.gunbang.market.domain.auction.entity.QAuction.auction;
import static no.gunbang.market.domain.auction.entity.QBid.bid;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.QAuction;
import no.gunbang.market.domain.auction.entity.QBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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

    @Override
    public Page<AuctionListResponseDto> findPopularBidItems(LocalDateTime startDate, Pageable pageable) {
        QBid bid = QBid.bid;
        QAuction auction = QAuction.auction;

        JPQLQuery<AuctionListResponseDto> query = queryFactory
            .select(Projections.fields(AuctionListResponseDto.class,
                auction.id.as("auctionId"),
                auction.item.id.as("itemId"),
                auction.item.name.as("itemName"),
                auction.startingPrice.as("startPrice"),
                bid.bidPrice.max().as("currentMaxPrice"),
                auction.dueDate.as("dueDate"),
                bid.id.count().as("bidCount")
            ))
            .from(bid)
            .join(bid.auction, auction)
            .where(bid.createdAt.goe(startDate))
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate)
            .orderBy(bid.id.count().desc());

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }
}
