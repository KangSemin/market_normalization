package no.gunbang.market.common;

import no.gunbang.market.common.lock.NamedLockImpl;
import no.gunbang.market.common.lock.OptimisticLockImpl;
import no.gunbang.market.common.lock.PessimisticLockImpl;
import no.gunbang.market.common.lock.ReentrantLockImpl;
import no.gunbang.market.common.lock.SynchronizedLockAdvancedImpl;
import no.gunbang.market.common.lock.SynchronizedLockImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LockStrategyConfig {

    @Bean
    @ConditionalOnProperty(name = "lock.strategy", havingValue = "synchronized")
    public SynchronizedLockImpl synchronizedLock() {
        return new SynchronizedLockImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "lock.strategy", havingValue = "synchronizedAdvanced")
    public SynchronizedLockAdvancedImpl synchronizedAdvancedLock() {
        return new SynchronizedLockAdvancedImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "lock.strategy", havingValue = "reentrant")
    public ReentrantLockImpl ReentrantLock() {
        return new ReentrantLockImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "lock.strategy", havingValue = "optimistic")
    public OptimisticLockImpl optimisticLock() {
        return new OptimisticLockImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "lock.strategy", havingValue = "pessimistic")
    public PessimisticLockImpl pessimisticLock() {
        return new PessimisticLockImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "lock.strategy", havingValue = "named")
    public NamedLockImpl NamedLock(JdbcTemplate jdbcTemplate) {
        return new NamedLockImpl(jdbcTemplate);
    }

    // TODO : Redis 사용 시작 후 주석 해제
//    @Bean
//    @ConditionalOnProperty(name = "lock.strategy", havingValue = "lettuce")
//    public RedisLettuceLockImpl RedisLettuceLock(
//        RedisClient redisClient,
//        StatefulRedisConnection<String, String> connection,
//        RedisCommands<String, String> commands)
//    {
//        return new RedisLettuceLockImpl(redisClient, connection, commands);
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "lock.strategy", havingValue = "redisson")
//    public RedisRedissonLockImpl RedisRedissonLock(RedissonClient redisClient)
//    {
//        return new RedisRedissonLockImpl(redisClient);
//    }
}
