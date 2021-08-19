package validation;

import annotations.InRange;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

/***
 * Класс ошибки, относящейся к аннотации нахождения в границах. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие
 * аннотации нахождения в границах.
 */
public class ErrorInRange implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является целочисленным типом или оберткой с помощью стрима. Иначе
     * выбрасывается исключение, потому что это некорректное использование аннотации.
     * Далее из аннотированного типа объекта извлекаются минимальное и
     * максимальное значения, происходит проверка того, что они адекватные
     * (min <= max, иначе выбрасывается исключение, так нельзя использовать аннотацию),
     * происходит сравнение объекта с границами.
     * В случае несоответствия создается экземпляр ошибки.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorInRange check(Object object, AnnotatedType type, String path) {
        Class actualType = ActualValidator.findClass(type.getType());
        Class[] possibleTypes = new Class[]{int.class, Integer.class, byte.class,
                Byte.class, short.class, Short.class, long.class, Long.class};

        if (Arrays.stream(possibleTypes).noneMatch(v -> v.equals(actualType))) {
            throw new IllegalArgumentException("InRange annotation is for integer numeric types");
        }

        long min = type.getAnnotation(InRange.class).min();
        long max = type.getAnnotation(InRange.class).max();

        if (min > max) {
            throw new IllegalArgumentException("Min value of InRange should be less than " +
                    "or equal to the Max value");
        }

        if (((Number) object).longValue() < min || ((Number) object).longValue() > max)
            return new ErrorInRange((Number) object, min, max, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must be in range from " + min + " to " + max;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedValue;
    }

    public ErrorInRange(Number failedValue, long min, long max, String path) {
        this.failedValue = failedValue;
        this.min = min;
        this.max = max;
        this.path = path;
    }

    private final Number failedValue;
    private final long min;
    private final long max;
    private final String path;
}
