package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorNotBlankTest {

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
        Object o = ErrorNotBlank.check(test1.blank,
                test1.getClass().getField("blank").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorNotBlank.check(test1.map,
                    test1.getClass().getField("map").getAnnotatedType(), "");
        });

        Object o2 = ErrorNotBlank.check(test1.string1,
                test1.getClass().getField("string1").getAnnotatedType(), "");
        assertNull(o2);
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotBlank.check(test1.blank,
                test1.getClass().getField("blank").getAnnotatedType(), "");
        assertEquals("Value must not be blank", ((ErrorNotBlank) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotBlank.check(test1.blank,
                test1.getClass().getField("blank").getAnnotatedType(), "test");
        assertNotNull(((ErrorNotBlank) o).getPath());
        assertEquals("test", ((ErrorNotBlank) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorNotBlank.check(test1.blank,
                test1.getClass().getField("blank").getAnnotatedType(), "");
        assertEquals("    ", ((ErrorNotBlank) o).getFailedValue());
    }
}