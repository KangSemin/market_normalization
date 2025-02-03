package no.gunbang.market.common.lock;


import java.util.function.Supplier;

public class SynchronizedLockImpl implements LockStrategy{

    // 모든 작업에 대해 동일한 전역 락 객체를 사용
    private final Object globalLock = new Object();

    @Override
    public boolean lock(String lockKey, long waitTime, long leaseTime) {
        // 전역 락은 별도 획득 로직 없이 사용
        return true;
    }

    @Override
    public void unlock(String lockKey) {
        // 전역 락은 synchronized 블록 종료 시 자동 해제됨
    }

    @Override
    public <T> T execute(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        // 전역 락으로 감싸므로, lockKey는 의미가 없음.
        synchronized (globalLock) {
            return supplier.get();
        }
    }

    @Override
    public <T> T execute(Class<T> entityClass, String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        return execute(lockKey, waitTime, leaseTime, supplier);
    }
}
