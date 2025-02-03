package no.gunbang.market.common.lock;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.function.Supplier;

public class OptimisticLockImpl implements LockStrategy {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean lock(String lockKey, long waitTime, long leaseTime) {
        return true;            // 별도의 획득 없음
    }

    @Override
    public void unlock(String lockKey) {
                                // 마찬가지로 transaction 종료 시 해제
    }

    @Override
    public <T> T execute(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        return null;
    }


    @Override
    public <T> T execute(Class<T> entityClass, Object lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {

        // 엔티티를 조회해서 버전확인
        entityManager.find(entityClass, lockKey, LockModeType.OPTIMISTIC);

        return supplier.get();
    }
}
