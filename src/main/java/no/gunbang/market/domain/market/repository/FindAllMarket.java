//package no.gunbang.market.domain.market.repository;
//
//public class FindAllMarket {
//
//    @Override
//    public Page<MarketListResponseDto> findAllMarketItems(String searchKeyword, String sortBy, String sortDirection, Pageable pageable) {
//        QMarket market = QMarket.market;
//        QTrade trade = QTrade.trade;
//        QItem item = QItem.item;
//
//        BooleanBuilder builder = new BooleanBuilder();
//        if (searchKeyword != null && !searchKeyword.isBlank()) {
//            builder.and(item.name.containsIgnoreCase(searchKeyword));
//        }
//        builder.and(market.status.ne(Status.COMPLETED))
//            .and(market.status.ne(Status.CANCELLED));
//
//        //서브쿼리용 alias
//        QMarket subMarket = new QMarket("subMarket");
//        QTrade subTrade = new QTrade("subTrade");
//
//        JPQLQuery<MarketListResponseDto> query = queryFactory
//            .select(new QMarketListResponseDto(
//                market.item.id,
//                market.item.name,
//                JPAExpressions
//                    .select(subMarket.amount.sum().coalesce(0))
//                    .from(subMarket)
//                    .where(subMarket.item.id.eq(market.item.id)),
//                JPAExpressions
//                    .select(subMarket.price.min().coalesce(0L))
//                    .from(subMarket)
//                    .where(subMarket.item.id.eq(market.item.id)),
//                JPAExpressions
//                    .select(subTrade.id.count().coalesce(0L))
//                    .from(subTrade)
//                    .where(subTrade.market.item.id.eq(market.item.id))
//            ))
//            .from(market)
//            .leftJoin(trade).on(market.id.eq(trade.market.id)).fetchJoin()
//            .leftJoin(item).on(market.item.id.eq(item.id)).fetchJoin()
//            .where(builder)
//            .groupBy(market.item.id, market.item.name)
//            .orderBy(determineSorting(sortBy, sortDirection, market))
//            .offset(pageable.getOffset())
//            .limit(pageable.getPageSize());
//
//        return PageableExecutionUtils.getPage(query.fetch(), pageable, () -> {
//            JPAQuery<Long> countQuery = queryFactory
//                .select(market.item.id.countDistinct())
//                .from(market)
//                .leftJoin(trade).on(market.id.eq(trade.market.id))
//                .leftJoin(item).on(market.item.id.eq(item.id))
//                .where(builder);
//            return Optional.ofNullable(countQuery.fetchOne()).orElse(0L);
//        });
//    }
//
//    private OrderSpecifier<?> determineSorting(String sortBy, String sortDirection, QMarket market) {
//        Order order = "DESC".equalsIgnoreCase(sortDirection) ? Order.DESC : Order.ASC;
//        QMarket subMarketSorting = new QMarket("subMarketSorting");
//        return switch (sortBy) {
//            case "itemName" -> new OrderSpecifier<>(order, market.item.name);
//            case "price" -> new OrderSpecifier<>(order,
//                JPAExpressions
//                    .select(subMarketSorting.price.min().coalesce(0L))
//                    .from(subMarketSorting)
//                    .where(subMarketSorting.item.id.eq(market.item.id))
//            );
//            case "amount" -> new OrderSpecifier<>(order,
//                JPAExpressions
//                    .select(subMarketSorting.amount.sum().coalesce(0))
//                    .from(subMarketSorting)
//                    .where(subMarketSorting.item.id.eq(market.item.id))
//            );
//            default -> new OrderSpecifier<>(Order.ASC, Expressions.numberTemplate(Long.class, "RAND()"));
//        };
//    }
//
//}
