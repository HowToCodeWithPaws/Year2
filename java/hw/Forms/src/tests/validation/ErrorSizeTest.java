package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorSizeTest {

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
        Object o = ErrorSize.check(test1.str,
                test1.getClass().getField("str").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorSize.check(test1.y,
                    test1.getClass().getField("y").getAnnotatedType(), "");
        });

        Object o2 = ErrorSize.check(test1.map,
                test1.getClass().getField("map").getAnnotatedType(), "");
        assertNull(o2);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorSize.check(test1.kstring,
                    test1.getClass().getField("kstring").getAnnotatedType(), "");
        });
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorSize.check(test1.str,
                test1.getClass().getField("str").getAnnotatedType(), "");
        assertEquals("Value must be of size from 1 to 5", ((ErrorSize) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorSize.check(test1.str,
                test1.getClass().getField("str").getAnnotatedType(), "test");
        assertNotNull(((ErrorSize) o).getPath());
        assertEquals("test", ((ErrorSize) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorSize.check(test1.str,
                test1.getClass().getField("str").getAnnotatedType(), "");
        assertEquals("Hello losers", ((ErrorSize) o).getFailedValue());
    }
}