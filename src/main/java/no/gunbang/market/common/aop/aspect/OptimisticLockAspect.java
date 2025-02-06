package no.gunbang.market.common.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OptimisticLockAspect {

    // 재시도 간 대기 시간 (밀리초)
    private static final long WAIT_TIME = 100;

    // 최대 재시도 횟수
    private static final int MAX_RETRIES = 10;

    @Around("@annotation(no.gunbang.market.common.aop.annotation.OptimisticLock)")
    public Object handleOptimisticLock(ProceedingJoinPoint joinPoint) throws Throwable {
        int attempts = 0;

        // 재시도 횟수만큼 반복
        while (attempts < MAX_RETRIES) {
            try {
                return joinPoint.proceed();  // 정상 실행
            } catch (ObjectOptimisticLockingFailureException e) {
                attempts++;
                Thread.sleep(WAIT_TIME);
            }
        }

        // 재시도 횟수 초과시 예외 던짐
        throw new ObjectOptimisticLockingFailureException("낙관적 락 충돌 발생", null);
    }
}