package no.gunbang.market.domain.market.cursor;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.Predicate;

public interface CursorStrategy {
    /**
     * 커서 조건을 생성해 반환
     * @param order       정렬 방향
     * @param lastItemId  마지막 아이템의 id (tie-breaker)
     * @param cursorValues 추가 커서 값들 (가격, 수량, 아이템명 등)
     * @return 커서 조건 Predicate
     */
    Predicate buildCursorPredicate(Order order, Long lastItemId, CursorValues cursorValues);
}
