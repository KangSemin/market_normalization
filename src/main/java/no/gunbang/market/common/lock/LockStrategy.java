package no.gunbang.market.common.lock;

import java.util.function.Supplier;

public interface LockStrategy {

    boolean lock(String lockKey, long waitTime, long leaseTime);

    void unlock(String lockKey);

    <T> T execute(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier);

    <T> T execute(Class<T> entityClass, String lockKey, long waitTime, long leaseTime, Supplier<T> supplier);
}
