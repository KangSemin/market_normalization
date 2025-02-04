//package no.gunbang.market.common.lock;
//
//import io.lettuce.core.RedisClient;
//import io.lettuce.core.ScriptOutputType;
//import io.lettuce.core.SetArgs;
//import io.lettuce.core.api.StatefulRedisConnection;
//import io.lettuce.core.api.sync.RedisCommands;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Supplier;
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//public class RedisLettuceLockImpl implements LockStrategy {
//
//    private final RedisClient redisClient;
//    private final StatefulRedisConnection<String, String> connection;
//    private final RedisCommands<String, String> commands;
//
//    // 각 락 키에 대해 락 소유자 토큰을 저장하는 Map
//    private final ConcurrentHashMap<String, String> tokenMap = new ConcurrentHashMap<>();
//
//    @Override
//    public boolean lock(String lockKey) {
//        // 고유 토큰 생성
//        String token = UUID.randomUUID().toString();
//        tokenMap.put(lockKey, token);
//
//        long end = System.currentTimeMillis() + WAIT_TIME;
//
//        while (System.currentTimeMillis() < end) {
//            // SET 명령어를 호출해서 lockKey가 존재하지 않을때 토큰 저장
//            // NX: 키가 존재하지 않을 경우에만 설정
//            // PX: 만료 시간을 밀리초 단위로 지정
//            String result = commands.set(lockKey, token, SetArgs.Builder.nx().px(LEASE_TIME));
//            if ("OK".equals(result)) {
//                return true;
//            }
//            try {
//                // 잠시 대기 후 재시도
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }
//        // 지정 시간 내에 락 획득에 실패하면 tokenMap에서 제거 후 false 반환
//        tokenMap.remove(lockKey);
//        return false;
//    }
//
//    @Override
//    public void unlock(String lockKey) {
//        String token = tokenMap.get(lockKey);
//        if (token == null) {
//            return;
//        }
//        // Lua 스크립트를 이용하여, 현재 저장된 토큰과 일치하는 경우에만 키를 삭제
//        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] " +
//            "then return redis.call('del', KEYS[1]) else return 0 end";
//        commands.eval(luaScript, ScriptOutputType.INTEGER, new String[]{lockKey}, token);
//        tokenMap.remove(lockKey);
//    }
//
//    @Override
//    public <T> T execute(String lockKey, Supplier<T> supplier) {
//        if (!lock(lockKey)) {
//            throw new RuntimeException("락 획득 실패");
//        }
//        try {
//            return supplier.get();
//        } finally {
//            unlock(lockKey);
//        }
//    }
//
//    @Override
//    public <T> T execute(Class<T> entityClass, String lockKey, Supplier<T> supplier) {
//        return execute(lockKey, supplier);
//    }
//}
