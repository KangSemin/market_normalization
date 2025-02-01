package no.gunbang.market.domain.auction.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
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
            .select(Projections.fields(AuctionHistoryResponseDto.class,
                auction.id.as("auctionId"),
                auction.item.id.as("itemId"),
                auction.item.name.as("itemName"),
                auction.startingPrice.as("startPrice"),

                new CaseBuilder()
                    .when(bid.user.id.eq(userId))
                    .then(bid.bidPrice)
                    .otherwise(0L)
                    .max()
                    .coalesce(0L)
                    .as("userBidPrice"),

                bid.bidPrice.max().coalesce(0L).as("currentMaxPrice"),

                new CaseBuilder()
                    .when(auction.user.id.eq(userId)).then("사용자가 판매자임")
                    .when(bid.user.id.eq(userId)).then("입찰 완료")
                    .otherwise("입찰 실패")
                    .as("bidStatus"),

                new CaseBuilder()
                    .when(auction.status.eq(Status.COMPLETED)).then("판매 완료")
                    .otherwise("판매 중")
                    .as("saleStatus"),

                auction.user.id.eq(userId).as("isSeller"),
                auction.dueDate.as("dueDate"),
                auction.status.as("status"),
                bid.user.id.as("bidUserId")
            ))
            .from(auction)
            .leftJoin(bid).on(auction.id.eq(bid.auction.id))
            .where(
                (userId != null ? auction.user.id.eq(userId) : auction.user.id.isNull())
                    .or(userId != null ? bid.user.id.eq(userId) : bid.user.id.isNull())
            )
            .groupBy(
                auction.id,
                auction.item.id,
                auction.item.name,
                auction.startingPrice,
                auction.dueDate,
                auction.status,
                auction.user.id,
                bid.user.id
            )
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
            .where(auction.createdAt.goe(startDate))
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate)
            .orderBy(bid.id.count().desc())
            .limit(100);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }
}
