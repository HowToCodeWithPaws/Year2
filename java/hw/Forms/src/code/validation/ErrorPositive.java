package validation;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

/***
 * Класс ошибки, относящейся к аннотации положительности. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие аннотации положительности.
 */
public class ErrorPositive implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является целочисленным типом или оберткой с помощью стрима.
     * Иначе выбрасывается исключение, потому что это некорректное использование аннотации.
     * Далее, если объект неположительный, создается и возвращается ошибка положительности.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorPositive check(Object object, AnnotatedType type, String path) {
        Class actualType = ActualValidator.findClass(type.getType());
        Class[] possibleTypes = new Class[]{int.class, Integer.class, byte.class,
                Byte.class, short.class, Short.class, long.class, Long.class};
        if (Arrays.stream(possibleTypes).noneMatch(v -> v.equals(actualType))) {
            throw new IllegalArgumentException("Positive annotation is for integer numeric types");
        }

        if (((Number) object).longValue() <= 0) return new ErrorPositive((Number) object, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must be positive";
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedNumber;
    }

    public ErrorPositive(Number failedNumber, String path) {
        this.failedNumber = failedNumber;
        this.path = path;
    }

    private final Number failedNumber;
    private final String path;

}
