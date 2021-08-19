package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorNotNullTest {

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
        Object o = ErrorNotNull.check(test1.list,
                test1.getClass().getField("list").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorNotNull.check(test1.y,
                    test1.getClass().getField("y").getAnnotatedType(), "");
        });

        Object o2 = ErrorNotNull.check(test1.str,
                test1.getClass().getField("str").getAnnotatedType(), "");
        assertNull(o2);
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotNull.check(test1.list,
                test1.getClass().getField("list").getAnnotatedType(), "");
        assertEquals("Value must not be null", ((ErrorNotNull) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotNull.check(test1.list,
                test1.getClass().getField("list").getAnnotatedType(), "test");
        assertNotNull(((ErrorNotNull) o).getPath());
        assertEquals("test", ((ErrorNotNull) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotNull.check(test1.list,
                test1.getClass().getField("list").getAnnotatedType(), "");
        assertNull(((ErrorNotNull) o).getFailedValue());
    }
}