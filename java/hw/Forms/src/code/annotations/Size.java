package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Аннотация размера - содержит поля минимального и максимального размера
 * с дефолтными значениями разумными для смысла размера.
 * Применима к строкам, листам, мапам и сетам.
 */
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    int min() default 0;

    int max() default Integer.MAX_VALUE;
}
