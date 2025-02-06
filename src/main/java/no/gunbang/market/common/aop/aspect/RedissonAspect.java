package no.gunbang.market.common.aop.aspect;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.aop.annotation.RedissonLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RedissonAspect {

    private final RedissonClient redissonClient;

    public RedissonAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(redissonLock)")
    public Object manageRedissonLock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        String key = redissonLock.key();
        long leaseTime = redissonLock.leaseTime();
        TimeUnit unit = redissonLock.unit();

        RLock lock = redissonClient.getLock(key);
        lock.lock(leaseTime, unit);

        try {
            log.info("락 획득 성공");
            return joinPoint.proceed();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
