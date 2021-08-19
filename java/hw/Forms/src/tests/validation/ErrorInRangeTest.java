package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorInRangeTest {

    /***
     * Кто и зачем читает комменты к тестам. Кто и зачем пишет комменты к тестам.
     * Помимо проверки методов каждой ошибки (сообщение, путь, значение) тестируется метод
     * проверки стандартным образом: проверяется, что на неверном значении действительно создается
     * ошибка, что на объекте неподходящего типа выбрасывается исключение, что на корректном значении
     * ошибка не создается. Также в случае параметров проверяется, что у аннотации с неверно написанными
     * параметрами будет выброшено исключение.
     */
    @Test
    void check() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorInRange.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorInRange.check(test1.str,
                    test1.getClass().getField("str").getAnnotatedType(), "");
        });

        Object o2 = ErrorInRange.check(test1.z,
                test1.getClass().getField("z").getAnnotatedType(), "");
        assertNull(o2);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorInRange.check(test1.k,
                    test1.getClass().getField("k").getAnnotatedType(), "");
        });
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorInRange.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "");
        assertEquals("Value must be in range from 1 to 20", ((ErrorInRange) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorInRange.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "test");
        assertNotNull(((ErrorInRange) o).getPath());
        assertEquals("test", ((ErrorInRange) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorInRange.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "");
        assertEquals(-5, ((ErrorInRange) o).getFailedValue());
    }
}