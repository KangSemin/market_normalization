package no.gunbang.market.common.lock;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.function.Supplier;

public class PessimisticLockImpl implements LockStrategy {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean lock(String lockKey, long waitTime, long leaseTime) {
        return true;    // 데이터베이스에서 제공하는 행 수준 락을 활용하므로 호출할 필요 X
    }

    @Override
    public void unlock(String lockKey) {
                        // 트랜잭션 종료 시 자동 해제
    }

    @Override
    public <T> T execute(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        return null;
    }

    @Override
    public <T> T execute(Class<T> entityClass, Object lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        T entity = entityManager.find(entityClass, lockKey, LockModeType.PESSIMISTIC_WRITE);
        if (entity == null) {
            throw new RuntimeException("엔티티를 찾을 수 없습니다.");
        }
        return supplier.get();
    }

}
