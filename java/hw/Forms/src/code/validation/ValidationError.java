package validation;

/***
 * Интерфейс для ошибки валидации: содержит методы
 * для получения сообщения об ошибке, пути к объекту,
 * с которым произошло нарушение, и не прошедшего проверку объекта.
 */
public interface ValidationError {
    String getMessage();

    String getPath();

    Object getFailedValue();
}
