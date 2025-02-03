package no.gunbang.market.common.aspect;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SchedulerAspect {

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void trackAuctionScheduler() {}

    @Around("trackAuctionScheduler()")
    public Object trackScheduleMethod(ProceedingJoinPoint jointPoint) throws Throwable {

        String methodName = jointPoint.getSignature().getName();

        long startsTime = System.nanoTime();
        LocalDateTime startsAt = LocalDateTime.now();

        Object result = jointPoint.proceed();

        long endsTime = System.nanoTime();
        LocalDateTime endsAt = LocalDateTime.now();

        long duration = endsTime - startsTime;

        log.info("Operated method: {}", methodName);
        log.info("startsAt: {}", startsAt);
        log.info("endsAt: {}", endsAt);
        log.info("execution time: {} ms", duration/1_000_000);

        return result;
    }
}