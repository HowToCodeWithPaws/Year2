package validation;

import java.lang.reflect.AnnotatedType;

/***
 * Класс ошибки, относящейся к аннотации не равенства null. Наследуется от интерфейса
 * ошибки валидации и содержит требуемые им методы сообщения, пути, объекта.
 * Кроме этого содержит статический метод проверки объекта на соответствие аннотации не равенства null.
 */
public class ErrorNotNull implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект не
     * является примитивным типом. Иначе выбрасывается исключение, потому что это некорректное
     * использование аннотации.
     * Далее если объект равен null создается и возвращается экземпляр ошибки.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorNotNull check(Object object, AnnotatedType type, String path) {
        if (ActualValidator.findClass(type.getType()).isPrimitive()) {
            throw new IllegalArgumentException("NotNull annotation is for reference types");
        }

        if (object == null) return new ErrorNotNull(object, path);
        return null;
    }

    @Override
    public String getMessage() {
        return "Value must not be null";
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedObject;
    }

    public ErrorNotNull(Object failedObject, String path) {
        this.failedObject = failedObject;
        this.path = path;
    }

    private final Object failedObject;
    private final String path;
}
