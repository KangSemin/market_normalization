package no.gunbang.market;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
public class TestUtils {

    private static <T> T createEntity(Class<T> clas, Map<String, Object> fieldValues) {
        try {
            T instance = clas.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
                try {
                    ReflectionTestUtils.setField(instance, entry.getKey(), entry.getValue());
                } catch (IllegalArgumentException e) {
                    log.info("⚠️ Warning: Field '{}' not found in class {}", entry.getKey(), clas.getName());
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("객체 생성 실패", e);
        }
    }

    public static <T> T spy(Class<T> clazz, Map<String, Object> fieldValues) {
        T entity = createEntity(clazz, fieldValues);
        return Mockito.spy(entity); //자동으로 spy() 적용
    }
}