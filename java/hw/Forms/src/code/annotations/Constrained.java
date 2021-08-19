package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Аннотация ограниченности - показатель того,
 * что аннотированный класс нужно проверять на
 * соответствие аннотациям в данной библиотеке.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constrained {
}
