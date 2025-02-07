package no.gunbang.market.common.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.aop.annotation.LettuceLock;
import no.gunbang.market.common.redis.LettuceLockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class LettuceAspect {

    private final LettuceLockService lettuceLockService;

    @Around("@annotation(lettuceLock)")
    public Object manageRedisSpinLock(ProceedingJoinPoint joinPoint, LettuceLock lettuceLock)
        throws Throwable {
        String key = lettuceLock.key();

        boolean acquired = false;

        while (!acquired) {
            log.info("락 획득 실패");
            Boolean result = lettuceLockService.lock(key);
            if (Boolean.TRUE.equals(result)) {
                acquired = true;
                break;
            }
            Thread.sleep(50);
        }

        try {
            log.info("락 획득 성공");
            return joinPoint.proceed();
        } finally {
            if (acquired) {
                lettuceLockService.unlock(key);
            }
        }
    }
}
