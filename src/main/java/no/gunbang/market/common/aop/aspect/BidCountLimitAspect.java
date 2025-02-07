package no.gunbang.market.common.aop.aspect;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.aop.annotation.BidCountLimit;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.request.BidAuctionRequestDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BidCountLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(bidCountLimit)")
    public Object around(ProceedingJoinPoint joinPoint, BidCountLimit bidCountLimit)
        throws Throwable {

        String limitPrefix = bidCountLimit.prefix();
        long ttl = bidCountLimit.ttlInMills();
        TimeUnit unit = bidCountLimit.unit();

        Object[] args = joinPoint.getArgs();

        Long userId = (Long) args[0];

        Long auctionId = ((BidAuctionRequestDto) args[1]).getAuctionId();

        String key = limitPrefix + userId + ":" + auctionId;

        String value = "LIMITED";

        String existingValue = redisTemplate.opsForValue().get(key);

        redisTemplate.opsForValue().set(key, value, ttl, unit);

        try {
            if (existingValue != null) {
                throw new CustomException(ErrorCode.CONSECUTIVE_BID_NOT_ALLOWED);
            }
            return joinPoint.proceed();

        } finally {
            redisTemplate.delete(key);
        }
    }
}