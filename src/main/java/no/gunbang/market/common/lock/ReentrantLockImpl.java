package no.gunbang.market.common.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ReentrantLockImpl implements LockStrategy{

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean lock(String lockKey, long waitTime, long leaseTime) {
        ReentrantLock lock = lockMap.computeIfAbsent(lockKey, k -> new ReentrantLock(true));
        try {
            return lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        ReentrantLock lock = lockMap.get(lockKey);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            // 대기 중인 스레드가 없으면 맵에서 제거
            if (!lock.hasQueuedThreads()) {
                lockMap.remove(lockKey);
            }
        }
    }

    @Override
    public <T> T execute(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {

        if (!lock(lockKey, waitTime, leaseTime)) {
            throw new RuntimeException("락 획득 실패: " + lockKey);
        }
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    @Override
    public <T> T execute(Class<T> entityClass, Object lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        return execute(String.valueOf(lockKey), waitTime, leaseTime, supplier);
    }
}
