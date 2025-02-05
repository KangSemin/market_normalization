package no.gunbang.market.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SemaphoreLock {
    String key();               // Redis에서 사용할 키
    int maxUsers() default 100; // 최대 동시 실행 가능 개수 (기본값: 100)
    long expireTime() default 5000; // 만료 시간 (밀리초, 기본값: 5초)
}