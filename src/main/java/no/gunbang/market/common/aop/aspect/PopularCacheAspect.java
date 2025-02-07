package no.gunbang.market.common.aop.aspect;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.aop.annotation.CacheablePopulars;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PopularCacheAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(no.gunbang.market.common.aop.annotation.CacheablePopulars)")
    public Object cachePopulars(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CacheablePopulars cacheAnnotation = AnnotationUtils.findAnnotation(signature.getMethod(), CacheablePopulars.class);

        if (cacheAnnotation == null) {
            return joinPoint.proceed();
        }

        String cacheKey = cacheAnnotation.cacheKey();
        int ttl = cacheAnnotation.ttl();

        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            log.info("📌 캐시 사용: {}", cacheKey);
            return cachedData;
        }

        Object result = joinPoint.proceed();

        if (result != null) {
            redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.MINUTES);
            log.info("📌 캐시 저장: {} (TTL: {}분)", cacheKey, ttl);
        }

        return result;
    }
}
