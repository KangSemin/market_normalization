package no.gunbang.market.common.lock;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.util.function.Supplier;
import org.hibernate.mapping.Selectable;

public class OptimisticLockImpl implements LockStrategy {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean lock(String lockKey) {
        return true;            // 별도의 획득 없음
    }

    @Override
    public void unlock(String lockKey) {
                                // 마찬가지로 transaction 종료 시 해제
    }

    @Override
    public <T> T execute(String lockKey, Supplier<T> supplier) {
        return null;
    }


    @Override
    public <T> T execute(Class<T> entityClass, String lockKey, Supplier<T> supplier) {

        String[] parts = lockKey.split(":");
        Long id = Long.valueOf(parts[parts.length-1]);

        // 엔티티를 조회해서 버전확인
        entityManager.find(entityClass, id, LockModeType.OPTIMISTIC);

        return supplier.get();
    }
}

