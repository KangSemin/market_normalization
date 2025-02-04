package no.gunbang.market.common.lock;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class NamedLockImpl implements LockStrategy{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean lock(String lockKey) {
        // MySQL timeout 단위는 초이므로, waitTime(ms)를 초로 변환
        int timeout = (int) (WAIT_TIME / 1000);
        Boolean result = jdbcTemplate.queryForObject(
            "SELECT GET_LOCK(?, ?)",
            new Object[]{lockKey, timeout},
            Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public void unlock(String lockKey) {
        jdbcTemplate.queryForObject("SELECT RELEASE_LOCK(?)", new Object[]{lockKey}, Boolean.class);
    }

    @Override
    public <T> T execute(String lockKey, Supplier<T> supplier) {

        if (!lock(lockKey)) {
            throw new RuntimeException("락 획득 실패");
        }
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    @Override
    public <T> T execute(Class<T> entityClass, String lockKey, Supplier<T> supplier) {
        return execute(lockKey, supplier);
    }
}
