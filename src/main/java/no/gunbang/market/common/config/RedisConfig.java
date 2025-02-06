package no.gunbang.market.common.config;

import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession // 세션 유지 30분
public class RedisConfig {

//    @Value("${SPRING_DATA_REDIS_URL:redis://redis:6379}")
    @Value("${spring.data.redis.url}") // 환경 변수 주입
    private String redisUri;

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(redisUri);
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUri);
        return Redisson.create(config);
    }
}