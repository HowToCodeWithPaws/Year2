package validation;

import annotations.AnyOf;

import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

/***
 * Класс ошибки, относящейся к аннотации соответствия одному из вариантов.
 * Наследуется от интерфейса ошибки валидации и содержит требуемые им
 * методы сообщения, пути, объекта. Кроме этого содержит статический метод
 * проверки объекта на соответствие данной аннотации.
 */
public class ErrorAnyOf implements ValidationError {

    /***
     * В методе проверки сначала проверяется то, что аннотированный объект
     * является строкой. Иначе выбрасывается исключение, потому что это некорректное
     * использование аннотации.
     * Далее из аннотированного типа объекта извлекаются возможные значения,
     * происходит сравнение объекта с массивом возможных с помощью стрима.
     * В случае никаких совпадений создается экземпляр ошибки.
     * @param object - проверяемый объект.
     * @param type - аннотированный тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @return - null, если ошибки нет, ошибка, если объект не соответствует аннотации.
     */
    public static ErrorAnyOf check(Object object, AnnotatedType type, String path) {
        if (!ActualValidator.findClass(type.getType()).equals(String.class)) {
            throw new IllegalArgumentException("AnyOf annotation is for String type");
        }

        String[] values = type.getAnnotation(AnyOf.class).value();

        if (Arrays.stream(values).noneMatch(v -> v.equals(object))) {
            return new ErrorAnyOf((String) object, values, path);
        }
        return null;
    }


    @Override
    public String getMessage() {
        StringBuilder options_ = new StringBuilder(options[0]);
        for (int i = 1; i < options.length - 1; ++i) {
            options_.append(", ").append(options[i]);
        }
        return "Value must be one of " + options_ + " or " + options[options.length - 1];
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Object getFailedValue() {
        return failedString;
    }

    public ErrorAnyOf(String failedString, String[] options, String path) {
        this.failedString = failedString;
        this.options = options;
        this.path = path;
    }

    private final String failedString;
    private final String path;
    private final String[] options;
}
