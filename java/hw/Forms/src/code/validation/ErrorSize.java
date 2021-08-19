package validation;

import annotations.Size;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * Класс ошибки, относящейся к аннотации размера. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие аннотации размера.
 */
public class ErrorSize implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является строкой, листом, сетом или мапой. Иначе выбрасывается исключение,
     * потому что это некорректное использование данной аннотации.
     * Далее из аннотированного типа объекта извлекаются минимальное и
     * максимальное значения размера, происходит проверка того, что они
     * адекватные (min <= max, иначе выбрасывается исключение, так нельзя использовать аннотацию),
     * происходит сравнение размера объекта с требованиями.
     * Если размер не соответствует требованиям, создается и возвращается экземпляр ошибки.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorSize check(Object object, AnnotatedType type, String path) {
        Class actualType = ActualValidator.findClass(type.getType());
        Class[] possibleTypes = new Class[]{String.class, List.class, Map.class, Set.class};
        if (Arrays.stream(possibleTypes).noneMatch(v -> v.equals(actualType))) {
            throw new IllegalArgumentException("Size annotation is for Lists," +
                    " Maps, Sets ans Strings types");
        }

        int min = type.getAnnotation(Size.class).min();
        int max = type.getAnnotation(Size.class).max();

        if (min > max) {
            throw new IllegalArgumentException("Min value of Size should be less" +
                    " than or equal to the Max value");
        }

        if (actualType.equals(Map.class) && (((Map) object).size() < min
                || ((Map) object).size() > max) ||
                actualType.equals(List.class) && (((List) object).size() < min
                        || ((List) object).size() > max) ||
                actualType.equals(Set.class) && (((Set) object).size() < min
                        || ((Set) object).size() > max) ||
                actualType.equals(String.class) && (((String) object).length() < min
                        || ((String) object).length() > max))
            return new ErrorSize(object, min, max, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must be of size from " + min + " to " + max;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedObject;
    }

    public ErrorSize(Object failedObject, long min, long max, String path) {
        this.failedObject = failedObject;
        this.min = min;
        this.max = max;
        this.path = path;
    }

    private final Object failedObject;
    private final long min;
    private final long max;
    private final String path;
}
