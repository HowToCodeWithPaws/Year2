package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.Unrelated;

import static org.junit.jupiter.api.Assertions.*;

class ErrorAnyOfTest {

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
        Object o = ErrorAnyOf.check(test1.string2,
                test1.getClass().getField("string2").getAnnotatedType(), "");
        assertNotNull(o);

        assertThrows(IllegalArgumentException.class, () -> {
            ErrorAnyOf.check(test1.y,
                    test1.getClass().getField("y").getAnnotatedType(), "");
        });

        Object o2 = ErrorAnyOf.check(test1.string1,
                test1.getClass().getField("string1").getAnnotatedType(), "");
        assertNull(o2);
    }

    @Test
    void getMessage() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorAnyOf.check(test1.string2,
                test1.getClass().getField("string2").getAnnotatedType(), "");
        assertEquals("Value must be one of a, b or c", ((ErrorAnyOf) o).getMessage());
    }

    @Test
    void getPath() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorAnyOf.check(test1.string2,
                test1.getClass().getField("string2").getAnnotatedType(), "test");
        assertNotNull(((ErrorAnyOf) o).getPath());
        assertEquals("test", ((ErrorAnyOf) o).getPath());
    }

    @Test
    void getFailedValue() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("Hello losers");
        Object o = ErrorAnyOf.check(test1.string2,
                test1.getClass().getField("string2").getAnnotatedType(), "");
        assertEquals("what", ((ErrorAnyOf) o).getFailedValue());
    }
}