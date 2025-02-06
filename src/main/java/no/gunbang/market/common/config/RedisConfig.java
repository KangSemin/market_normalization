package no.gunbang.market.common.config;

import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession //세션유지 30분
public class RedisConfig {

    @Bean
    public RedisClient redisClient() {
        String redisUri = "redis://127.0.0.1:6379";
        return RedisClient.create(redisUri);
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://127.0.0.1:6379");

        return Redisson.create(config);
    }
}