package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorPositiveTest {

    /***
     * Кто и зачем читает комменты к тестам. Кто и зачем пишет комменты к тестам.
     * Помимо проверки методов каждой ошибки (сообщение, путь, значение) тестируется метод
     * проверки стандартным образом: проверяется, что на неверном значении действительно создается
     * ошибка, что на объекте неподходящего типа выбрасывается исключение, что на корректном значении
     * ошибка не создается.
     */
    @Test
    void check() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorPositive.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorPositive.check(test1.str,
                    test1.getClass().getField("str").getAnnotatedType(), "");
        });

        Object o2 = ErrorPositive.check(test1.z,
                test1.getClass().getField("z").getAnnotatedType(), "");
        assertNull(o2);
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorPositive.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "");
        assertEquals("Value must be positive", ((ErrorPositive) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorPositive.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "test");
        assertNotNull(((ErrorPositive) o).getPath());
        assertEquals("test", ((ErrorPositive) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorPositive.check(test1.y,
                test1.getClass().getField("y").getAnnotatedType(), "");
        assertEquals(-5, ((ErrorPositive) o).getFailedValue());
    }
}