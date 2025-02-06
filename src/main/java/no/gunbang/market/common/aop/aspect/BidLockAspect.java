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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BidLockAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(bidCountLimit)")
    public Object around(ProceedingJoinPoint joinPoint, BidCountLimit bidCountLimit)
        throws Throwable {
        String limitPrefix = bidCountLimit.prefix();
        long ttl = bidCountLimit.ttlInMills();

        Object[] args = joinPoint.getArgs();

        Long userId = (Long) args[0];

        Long auctionId = ((BidAuctionRequestDto) args[1]).getAuctionId();


        String key = limitPrefix + userId + ":" + auctionId;

        String value = "LIMITED";

        log.info("$$$$$ Generated key: {}, value: {}", key, value);


        Boolean hasLimit = redisTemplate.opsForValue()
            .setIfAbsent(
                key,
                value,
                ttl,
                TimeUnit.MILLISECONDS
            );

        log.info("$$$$$$$ setIfAbsent returned: {}", hasLimit);


        boolean hasAlreadyLimit = Boolean.FALSE.equals(hasLimit);

        log.info("$$$$$$$ setIfAbsent returned: {}", hasAlreadyLimit);

        if (hasAlreadyLimit) {
            throw new CustomException(ErrorCode.CONSECUTIVE_BID_NOT_ALLOWED);
        }

        try {
            return joinPoint.proceed();
        } finally {
            redisTemplate.delete(key);
        }
    }
}