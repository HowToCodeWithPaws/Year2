package validation;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

/***
 * Класс ошибки, относящейся к аннотации отрицательности. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие аннотации отрициательности.
 */
public class ErrorNegative implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является целочисленным типом или оберткой с помощью стрима.
     * Иначе выбрасывается исключение, потому что это некорректное использование аннотации.
     * Далее, если объект неотрицательный, создается и возвращается ошибка отрицательности.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorNegative check(Object object, AnnotatedType type, String path) {
        Class actualType = ActualValidator.findClass(type.getType());
        Class[] possibleTypes = new Class[]{int.class, Integer.class, byte.class,
                Byte.class, short.class, Short.class, long.class, Long.class};

        if (Arrays.stream(possibleTypes).noneMatch(v -> v.equals(actualType))) {
            throw new IllegalArgumentException("Negative annotation is for integer numeric types");
        }

        if (((Number) object).longValue() >= 0) return new ErrorNegative((Number) object, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must be negative";
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedNumber;
    }

    public ErrorNegative(Number failedNumber, String path) {
        this.failedNumber = failedNumber;
        this.path = path;
    }

    private final Number failedNumber;
    private final String path;
}
