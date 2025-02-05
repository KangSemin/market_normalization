package no.gunbang.market.common.aspect;

import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.redis.DistributedSemaphoreService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SemaphoreAspect {

    private final DistributedSemaphoreService semaphoreService;

    public SemaphoreAspect(DistributedSemaphoreService semaphoreService) {
        this.semaphoreService = semaphoreService;
    }

    @Around("@annotation(semaphoreLock)")
    public Object manageSemaphore(ProceedingJoinPoint joinPoint, SemaphoreLock semaphoreLock) throws Throwable {
        String key = semaphoreLock.key();
        int maxUsers = semaphoreLock.maxUsers();
        long expireTime = semaphoreLock.expireTime();

        boolean acquired = false;
        try {
            acquired = semaphoreService.tryAcquire(key, maxUsers, expireTime);

            if (!acquired) {
                throw new RuntimeException("현재 트래픽이 많아 잠시 후 다시 시도해주세요.");
            }

            return joinPoint.proceed(); // 실제 메서드 실행
        } finally {
            if (acquired) {
                semaphoreService.release(key);
            }
        }
    }
}