package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorNotEmptyTest {

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
        Object o = ErrorNotEmpty.check(test1.empty,
                test1.getClass().getField("empty").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorNotEmpty.check(test1.y,
                    test1.getClass().getField("y").getAnnotatedType(), "");
        });

        Object o2 = ErrorNotEmpty.check(test1.map,
                test1.getClass().getField("map").getAnnotatedType(), "");
        assertNull(o2);
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotEmpty.check(test1.empty,
                test1.getClass().getField("empty").getAnnotatedType(), "");
        assertEquals("Value must not be empty", ((ErrorNotEmpty) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotEmpty.check(test1.empty,
                test1.getClass().getField("empty").getAnnotatedType(), "test");
        assertNotNull(((ErrorNotEmpty) o).getPath());
        assertEquals("test", ((ErrorNotEmpty) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotEmpty.check(test1.empty,
                test1.getClass().getField("empty").getAnnotatedType(), "");
        assertEquals("", ((ErrorNotEmpty) o).getFailedValue());
    }
}