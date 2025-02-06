package no.gunbang.market.common.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BidCountLimit {

    String prefix() default "bid_count_limit:";

    long ttlInMills() default 100000L;
}