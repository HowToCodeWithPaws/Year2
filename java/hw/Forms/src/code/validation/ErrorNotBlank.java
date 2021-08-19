package validation;

import java.lang.reflect.AnnotatedType;

/***
 * Класс ошибки, относящейся к аннотации незаполненности. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие аннотации незаполенности.
 */
public class ErrorNotBlank implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является строкой. Иначе выбрасывается исключение, потому что это некорректное
     * использование аннотации. Далее если isBlank на проверяемой строке
     * возвращает true, создается и возвращается экземпляр ошибки.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorNotBlank check(Object object, AnnotatedType type, String path) {

        if (!ActualValidator.findClass(type.getType()).equals(String.class)) {
            throw new IllegalArgumentException("NotBlank annotation is for String type");
        }

        if (((String) object).isBlank()) return new ErrorNotBlank((String) object, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must not be blank";
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedString;
    }

    public ErrorNotBlank(String failedString, String path) {
        this.failedString = failedString;
        this.path = path;
    }

    private final String failedString;
    private final String path;
}
