package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Аннотация границ - содержит поля минимального и максимального размера
 * с дефолтными значениями разумными для смысла границ, в которых может лежать число.
 * Применяется к целочисленным типам и их оберткам, гарантирует, что число лежит между
 * левой и правой границей включительно.
 */
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InRange {
    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;
}
