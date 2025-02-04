//package no.gunbang.market.common.lock;
//
//import java.util.concurrent.TimeUnit;
//import java.util.function.Supplier;
//import lombok.RequiredArgsConstructor;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//
//@RequiredArgsConstructor
//public class RedisRedissonLockImpl implements LockStrategy{
//
//    private final RedissonClient redissonClient;
//
//    @Override
//    public boolean lock(String lockKey) {
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            return lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return false;
//        }
//    }
//
//    @Override
//    public void unlock(String lockKey) {
//        RLock lock = redissonClient.getLock(lockKey);
//        if (lock.isHeldByCurrentThread()) {
//            lock.unlock();
//        }
//    }
//
//    @Override
//    public <T> T execute(String lockKey, Supplier<T> supplier) {
//        if (!lock(lockKey)) {
//            throw new RuntimeException("락 획득 실패");
//        }
//        try {
//            return supplier.get();
//        } finally {
//            unlock(lockKey);
//        }
//    }
//
//    @Override
//    public <T> T execute(Class<T> entityType, String lockKey, Supplier<T> supplier) {
//        return execute(lockKey, supplier);
//    }
//}
