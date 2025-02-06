package no.gunbang.market;

import java.util.Map;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class TestUtils {

    private static <T> T createEntity(Class<T> clas, Map<String, Object> fieldValues) {
        try {
            T instance = clas.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
                try {
                    ReflectionTestUtils.setField(instance, entry.getKey(), entry.getValue());
                } catch (IllegalArgumentException e) {
                    System.err.println("⚠️ Warning: Field '" + entry.getKey() + "' not found in class " + clas.getName());
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