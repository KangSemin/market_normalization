package no.gunbang.market.domain.auction.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.QAuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.QAuctionListResponseDto;
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
    public List<AuctionHistoryResponseDto> findUserAuctionHistory(Long userId) {
        QAuction auction = QAuction.auction;
        QBid bid = QBid.bid;

        return queryFactory
            .select(new QAuctionHistoryResponseDto(
                auction.id,
                auction.item.id,
                auction.item.name,
                auction.startingPrice,
                new CaseBuilder()
                    .when(bid.user.id.eq(userId))
                    .then(bid.bidPrice)
                    .otherwise(0L)
                    .max()
                    .coalesce(0L),
                bid.bidPrice.max().coalesce(0L),
                new CaseBuilder()
                    .when(auction.user.id.eq(userId)).then("사용자가 판매자임")
                    .when(bid.user.id.eq(userId)).then("입찰 완료")
                    .otherwise("입찰 실패"),
                new CaseBuilder()
                    .when(auction.status.eq(Status.COMPLETED)).then("판매 완료")
                    .otherwise("판매 중"),
                auction.user.id.eq(userId),
                auction.dueDate,
                auction.status
            ))
            .from(auction)
            .leftJoin(bid).on(auction.id.eq(bid.auction.id))
            .where(auction.user.id.eq(userId).or(bid.user.id.eq(userId)))
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate, auction.status, auction.user.id, bid.user.id)
            .fetch();
    }

    @Override
    public Page<AuctionListResponseDto> findPopularBidItems(LocalDateTime startDate, Pageable pageable) {
        QBid bid = QBid.bid;
        QAuction auction = QAuction.auction;

        JPQLQuery<AuctionListResponseDto> query = queryFactory
            .select(new QAuctionListResponseDto(
                auction.id,
                auction.item.id,
                auction.item.name,
                auction.startingPrice,
                bid.bidPrice.max(),
                auction.dueDate,
                bid.id.count()
            ))
            .from(bid)
            .join(bid.auction, auction)
            .where(auction.createdAt.goe(startDate))
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate)
            .orderBy(bid.id.count().desc())
            .limit(100);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }
}
