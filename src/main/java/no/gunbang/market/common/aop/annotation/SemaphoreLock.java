package no.gunbang.market.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SemaphoreLock {

    String key();               // Redis에서 사용할 키

    int maxUsers() default 100; // 최대 동시 실행 가능 개수 (기본값: 100)

    long waitTime() default 1000; // 락 획득 대기시간

    TimeUnit unit() default TimeUnit.MILLISECONDS;
}