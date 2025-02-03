package no.gunbang.market.common.aspect;

import java.time.Duration;
import java.time.Instant;
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

    // Pointcut 정의: @Scheduled 어노테이션이 달린 메서드 추적
    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void trackAuctionScheduler() {}

    @Around("trackAuctionScheduler()")
    public Object trackScheduleMethod(ProceedingJoinPoint jointPoint) throws Throwable {

        // 실행 중인 메서드 이름을 가져옴
        String methodName = jointPoint.getSignature().getName();

        // 메서드 실행 시작 시점의 Instant 기록
        Instant startsAt = Instant.now();

        // 실제 메서드 실행
        Object result = jointPoint.proceed();

        // 메서드 실행 종료 시점의 Instant 기록
        Instant endsAt = Instant.now();

        // 실행 시작과 끝 사이의 Duration 계산
        Duration duration = Duration.between(startsAt, endsAt);

        // Duration 값을 초 단위로 변환 후
        long seconds = duration.getSeconds();

        // 전체 초를 분과 시간으로 변환
        long minutes = seconds / 60;
        long hours = minutes / 60;

        // 나머지 분과 초를 계산
        minutes = minutes % 60;
        seconds = seconds % 60;

        // 로그에 메서드 정보와 실행 시간을 출력
        log.info("Operated Method: {}", methodName);
        log.info("Starts At: {}", startsAt);
        log.info("Ends At: {}", endsAt);
        log.info("Execution Time: {} hours {} minutes {} seconds", hours, minutes, seconds);

        // 실제 메서드 실행 결과 반환
        return result;
    }
}