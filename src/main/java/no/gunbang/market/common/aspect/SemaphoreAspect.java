package no.gunbang.market.common.aspect;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.common.redis.SemaphoreService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SemaphoreAspect {

    private final SemaphoreService semaphoreService;

    @Around("@annotation(semaphoreLock)")
    public Object manageSemaphore(ProceedingJoinPoint joinPoint, SemaphoreLock semaphoreLock) throws Throwable {
        String key = semaphoreLock.key();
        int maxUsers = semaphoreLock.maxUsers();
        long waitTime = semaphoreLock.waitTime();
        TimeUnit timeUnit = semaphoreLock.unit();

        boolean acquired = false;
        try {
            acquired = semaphoreService.tryAcquire(key, maxUsers, waitTime, timeUnit);
            log.info("현재 동시 사용자 수 : {}", maxUsers - semaphoreService.availablePermits(key));
            if (!acquired) {
                log.info("현재 트래픽이 많아 잠시 후 다시 시도 해 주세요");
                throw new CustomException(ErrorCode.TOO_MANY_TRAFFIC);
            }

            return joinPoint.proceed();
        } finally {
            if (acquired) {

                semaphoreService.release(key);
            }
        }
    }
}