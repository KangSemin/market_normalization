package no.gunbang.market.unit.domain.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import no.gunbang.market.common.redis.SemaphoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class SemaphoreServiceTest {

    @Autowired
    private SemaphoreService semaphoreService;

    AtomicInteger batchCount = new AtomicInteger(0);
    String semaphoreKey = "key";

    @Test
    public void testAvailabilityOfSemaphoreLock() throws InterruptedException {

        int totalRequests = 1000; // 총 요청 수
        int totalThreads = 150; // 총 스레드 수

        CountDownLatch latch = new CountDownLatch(totalRequests);

        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

        for (int i = 0; i < totalRequests; i++) {
            // 각 스레드에서 입찰 요청 실행
            executorService.execute(
                () ->
                {
                    try {
                        operateSemaphoreLock();
                        int batch = batchCount.incrementAndGet();
                        log.info("스레드: {}", batch);
                    } finally {
                        latch.countDown();
                    }
                }
            );

        }
        latch.await();

        executorService.shutdown();
    }

    // 어노테이션 대신 직접 semaphoreService 주입 후 실행
    private void operateSemaphoreLock() {

        semaphoreService.tryAcquire(
            semaphoreKey,
            100,
            1000,
            TimeUnit.MILLISECONDS
        );

        try {
            Thread.sleep(2000L);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        semaphoreService.release(semaphoreKey);
    }
}
