package no.gunbang.market.common;

import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession //세션유지 30분
public class RedisConfig {

    @Bean
    public RedisClient redisClient(@Value("${spring.data.redis.url}") String redisUri) {
        return RedisClient.create(redisUri);
    }
}