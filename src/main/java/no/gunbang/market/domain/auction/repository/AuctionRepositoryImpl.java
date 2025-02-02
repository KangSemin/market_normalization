package no.gunbang.market.domain.auction.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.QItem;
import no.gunbang.market.common.Status;
import no.gunbang.market.domain.auction.dto.response.AuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.response.QAuctionHistoryResponseDto;
import no.gunbang.market.domain.auction.dto.response.QAuctionListResponseDto;
import no.gunbang.market.domain.auction.entity.QAuction;
import no.gunbang.market.domain.auction.entity.QBid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionRepositoryCustom {

    private static final int POPULAR_LIMIT = 200;

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
                bid.bidPrice.coalesce(0L),
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
            .where(auction.status.ne(Status.CANCELLED))
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate, auction.status, auction.user.id, bid.bidPrice)
            .fetch();
    }

    @Override
    public Page<AuctionListResponseDto> findPopularAuctionItems(LocalDateTime startDate, Pageable pageable) {
        QBid bid = QBid.bid;
        QAuction auction = QAuction.auction;

        BooleanBuilder builder = new BooleanBuilder();
        builder
            .and(auction.status.ne(Status.COMPLETED))
            .and(auction.status.ne(Status.CANCELLED))
            .and(auction.createdAt.goe(startDate));

        JPQLQuery<AuctionListResponseDto> query = queryFactory
            .select(new QAuctionListResponseDto(
                auction.id,
                auction.item.id,
                auction.item.name,
                auction.startingPrice,
                bid.bidPrice.coalesce(0L),
                auction.dueDate,
                auction.bidderCount
            ))
            .from(bid)
            .join(bid.auction, auction)
            .where(builder)
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate, bid.bidPrice, auction.bidderCount)
            .orderBy(auction.bidderCount.desc())
            .limit(POPULAR_LIMIT);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public Page<AuctionListResponseDto> findAllAuctionItems(LocalDateTime startDate, String searchKeyword, String sortBy, String sortDirection, Pageable pageable) {
        QAuction auction = QAuction.auction;
        QBid bid = QBid.bid;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();
        if (searchKeyword != null && !searchKeyword.isBlank()) {
            builder.and(auction.item.name.containsIgnoreCase(searchKeyword));
        }
        builder
            .and(auction.status.ne(Status.COMPLETED))
            .and(auction.status.ne(Status.CANCELLED))
            .and(auction.createdAt.goe(startDate));

        List<AuctionListResponseDto> results = queryFactory
            .select(new QAuctionListResponseDto(
                auction.id,
                auction.item.id,
                auction.item.name,
                auction.startingPrice,
                bid.bidPrice.coalesce(0L),
                auction.dueDate,
                auction.bidderCount
            ))
            .from(auction)
            .leftJoin(bid).on(auction.id.eq(bid.auction.id))
            .leftJoin(item).on(auction.item.id.eq(item.id))
            .where(builder)
            .groupBy(auction.id, auction.item.id, auction.item.name, auction.startingPrice, auction.dueDate, bid.bidPrice, auction.bidderCount)
            .orderBy(determineSorting(sortBy, sortDirection, auction, bid))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return PageableExecutionUtils.getPage(results, pageable, () -> {
            JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction);

            if (builder.hasValue()) {
                countQuery.where(builder);
            }

            return Optional.ofNullable(countQuery.fetchOne()).orElse(0L);
        });
    }

    private OrderSpecifier<?> determineSorting(String sortBy, String sortDirection, QAuction auction, QBid bid) {
        Order order = "DESC".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
        return switch (sortBy) {
            case "itemName" -> new OrderSpecifier<>(order, auction.item.name);
            case "startPrice" -> new OrderSpecifier<>(order, auction.startingPrice);
            case "currentMaxPrice" -> new OrderSpecifier<>(order, bid.bidPrice.coalesce(0L));
            case "dueDate" -> new OrderSpecifier<>(order, auction.dueDate);
            default -> new OrderSpecifier<>(Order.ASC, Expressions.numberTemplate(Long.class, "RAND()"));
        };
    }
}
