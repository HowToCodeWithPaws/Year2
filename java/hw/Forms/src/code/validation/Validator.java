package validation;

import java.util.Set;

/***
 * Интерфейс для валидатора, который гарантирует наличие метода проверки
 * с требуемой сигнатурой: получает объект и возвращает сет ошибок валидации.
 */
public interface Validator {
    Set<ValidationError> validate(Object object);
}
