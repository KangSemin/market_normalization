//package no.gunbang.market.common;
//
//import io.lettuce.core.RedisClient;
//import io.lettuce.core.api.StatefulRedisConnection;
//import io.lettuce.core.api.sync.RedisCommands;
//import org.redisson.api.RedissonClient;
//import org.redisson.client.RedisException;
//import org.redisson.config.Config;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//
//@Configuration
//@EnableRedisHttpSession //세션유지 30분
//public class RedisConfig {
//
//    @Bean
//    public RedisClient redisClient(@Value("${spring.data.redis.url}") String redisUrl) {
//        return RedisClient.create(redisUrl);
//    }
//
//    @Bean
//    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
//        return redisClient.connect();
//    }
//
//    @Bean
//    public RedisCommands<String, String> redisCommands(StatefulRedisConnection<String, String> connection) {
//        return connection.sync();
//    }
//
//    @Bean
//    public RedissonClient redissonClient() {
//        try {
//            // Configure RedissonClient with Redis server URL and other settings
//            Config config = new Config();
//            config.useSingleServer().setAddress("redis://localhost:6379"); // Replace with your Redis URL
//            return org.redisson.Redisson.create(config);
//        } catch (RedisException e) {
//            throw new RuntimeException("Failed to initialize Redisson client", e);
//        }
//    }
//}


//TODO: 유저권한인증 AOP개발, 현재 userId가 null일시 500에러 뱉음
//TODO: redis 관련 주석은 docker 사용 가능 할 때부터 해제할 것.

