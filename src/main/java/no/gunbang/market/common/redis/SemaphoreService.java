package no.gunbang.market.common.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SemaphoreService {

    private final RedissonClient redissonClient;

    public boolean tryAcquire(String semaphoreKey, int permits, long waitTime, TimeUnit unit) {

        log.info("key: {}", semaphoreKey);

        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
        semaphore.trySetPermits(permits);
        try {

            return semaphore.tryAcquire(1, waitTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void release(String semaphoreKey) {
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
        semaphore.release(1);
    }

    public long availablePermits(String semaphoreKey) {
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKey);
        return semaphore.availablePermits();
    }
}
