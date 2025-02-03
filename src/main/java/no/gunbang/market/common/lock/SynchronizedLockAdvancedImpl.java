package no.gunbang.market.common.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SynchronizedLockAdvancedImpl implements LockStrategy {

    // lockKey별로 동기화할 객체를 관리
    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean lock(String lockKey, long waitTime, long leaseTime) {
        return true;        // 별도의 획득 로직이 없다
    }

    @Override
    public void unlock(String lockKey) {
                            // 역시 synchronized 블록을 벗어나면 자동으로 해제
    }

    @Override
    public <T> T execute(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        // lockKey 별로 고유한 락 객체를 사용하여 동기화
        Object lockObject = lockMap.computeIfAbsent(lockKey, k -> new Object());

        synchronized (lockObject) {
            return supplier.get();
        }
    }

    @Override
    public <T> T execute(Class<T> entityClass, String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        return execute(lockKey, waitTime, leaseTime, supplier);
    }
}
