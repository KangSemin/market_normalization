package no.gunbang.market.common.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class DistributedSemaphoreService {

    private final RedisCommands<String, String> redisCommands;

    public DistributedSemaphoreService(RedisClient redisClient) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        this.redisCommands = connection.sync();
    }

    public boolean tryAcquire(String semaphoreKey, int maxUsers, long expireTimeMillis) {
        synchronized (this) { // 동기화 처리
            long currentCount = redisCommands.incr(semaphoreKey);
            log.info("현재 접속자 수 : {}", currentCount);

            if (currentCount == 1) {
                redisCommands.pexpire(semaphoreKey, expireTimeMillis);
            }

            if (currentCount > maxUsers) {
                redisCommands.decr(semaphoreKey);
                return false;
            }
            return true;
        }
    }

    public void release(String semaphoreKey) {
        redisCommands.decr(semaphoreKey);
    }
}
