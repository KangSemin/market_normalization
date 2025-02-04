package no.gunbang.market.common.lock;

import java.util.function.Supplier;

public interface LockStrategy {

    long WAIT_TIME = 1000L;
    long LEASE_TIME = 3000L;

    boolean lock(String lockKey);

    void unlock(String lockKey);

    <T> T execute(String lockKey, Supplier<T> supplier);

    <T> T execute(Class<T> entityClass, String lockKey, Supplier<T> supplier);
}
