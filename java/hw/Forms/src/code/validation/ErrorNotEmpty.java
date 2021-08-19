package validation;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * Класс ошибки, относящейся к аннотации непустоты. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие аннотации непустоты.
 */
public class ErrorNotEmpty implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является строкой, листом, сетом или мапой. Иначе выбрасывается исключение,
     * потому что это некорректное использование данной аннотации.
     * Далее проверяется, что размер объекта не нулевой, иначе создается и
     * возвращается экземпляр ошибки.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorNotEmpty check(Object object, AnnotatedType type, String path) {
        Class actualType = ActualValidator.findClass(type.getType());
        Class[] possibleTypes = new Class[]{String.class, List.class, Map.class, Set.class};

        if (Arrays.stream(possibleTypes).noneMatch(v -> v.equals(actualType))) {
            throw new IllegalArgumentException("NotEmpty annotation is for Lists," +
                    " Maps, Sets ans Strings types");
        }

        if (actualType.equals(Map.class) && ((Map) object).isEmpty() ||
                actualType.equals(List.class) && ((List) object).isEmpty() ||
                actualType.equals(Set.class) && ((Set) object).isEmpty() ||
                actualType.equals(String.class) && ((String) object).isEmpty())
            return new ErrorNotEmpty(object, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must not be empty";
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedObject;
    }

    public ErrorNotEmpty(Object failedObject, String path) {
        this.failedObject = failedObject;
        this.path = path;
    }

    private final Object failedObject;
    private final String path;
}
