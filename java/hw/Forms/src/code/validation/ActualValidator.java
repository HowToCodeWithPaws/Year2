package validation;

import annotations.*;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * Класс настоящего валидатора, наследует интерфейс валидатора.
 * Содержит несколько методов для валидации объекта со всеми его
 * нюансами, вложенностями и прочим.
 */
public class ActualValidator implements Validator {

    /***
     * Метод для получения класса объекта из аннотированного типа
     * - нужно невероятно часто.
     * @param type - тип объекта, иногда может быть аннотированным.
     * @return - класс, к которому относится объект.
     */
    public static Class<?> findClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return findClass(((ParameterizedType) type).getRawType());
        }
        throw new IllegalArgumentException("this should literally never happen");
    }

    /***
     * Метод для вызовов проверки от конкретного класса ошибки валидации,
     * соответствующего проверяемой аннотации. В любом случае проверяется аннотация
     * NotNull, если объект ей аннотирован, остальные же проверяются только если
     * объект не null (в таком случае все значения легальны). Если проверка возвращает
     * не null, а ошибку, то ошибка добавляется к сету.
     * @param object - проверяемый объект.
     * @param annotatedType - тип проверяемого объекта.
     * @param annotationName - название проверяемой аннотации.
     * @param path - накопленный путь к объекту.
     * @param errors - сет ошибок, которые мы собираем.
     */
    void switchChecker(Object object, AnnotatedType annotatedType, String annotationName,
                       String path, Set<ValidationError> errors) {

        Object error;
        if (annotationName.equals("NotNull")) {
            if ((error = ErrorNotNull.check(object, annotatedType, path)) != null) {
                errors.add((ErrorNotNull) error);
            }
        }

        if (object != null) {
            switch (annotationName) {
                case "AnyOf":
                    if ((error = ErrorAnyOf.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorAnyOf) error);
                    }
                    break;
                case "InRange":
                    if ((error = ErrorInRange.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorInRange) error);
                    }
                    break;
                case "Negative":
                    if ((error = ErrorNegative.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorNegative) error);
                    }
                    break;
                case "Positive":
                    if ((error = ErrorPositive.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorPositive) error);
                    }
                    break;
                case "NotBlank":
                    if ((error = ErrorNotBlank.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorNotBlank) error);
                    }
                    break;
                case "NotEmpty":
                    if ((error = ErrorNotEmpty.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorNotEmpty) error);
                    }
                    break;
                case "Size":
                    if ((error = ErrorSize.check(object, annotatedType, path)) != null) {
                        errors.add((ErrorSize) error);
                    }
                    break;
            }
        }
    }

    /***
     * Метод для проверки того, что класс текущего объекта сам должен
     * подвергаться проверке. Если это правда, то мы вызываем проверку для
     * каждого поля текущего объекта. Путь формируем с добавлением точки
     * и названия поля для каждого вложенного поля.
     * @param object - проверяемый объект.
     * @param annotatedType - тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @param errors - сет ошибок, которые мы собираем.
     */
    void checkConstrained(Object object, AnnotatedType annotatedType,
                          String path, Set<ValidationError> errors) {
        if (findClass(annotatedType.getType()).isAnnotationPresent(Constrained.class)
                && object != null) {
            Field[] fields = findClass(annotatedType.getType()).getDeclaredFields();
            for (Field fielding : fields) {
                try {
                    fielding.setAccessible(true);
                    actuallyValidate(fielding.get(object),
                            fielding.getAnnotatedType(), path + "." +
                                    fielding.getName(), errors);
                } catch (IllegalAccessException e) {
                    //this should never actually happen
                    System.out.println("there was an illegal access " + e.getMessage());
                }
            }
        }
    }

    /***
     * Метод для проверки того, является ли текущий объект листом.
     * Если является, мы проверяем его элементы на соответствие указанным
     * параметрам-аннотациям. Путь формируем с добавлением квадратных
     * скобок и порядкового номера элемента в листе.
     * @param object - проверяемый объект.
     * @param annotatedType - тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @param errors - сет ошибок, которые мы собираем.
     */
    void checkList(Object object, AnnotatedType annotatedType,
                   String path, Set<ValidationError> errors) {
        if (List.class.isAssignableFrom(findClass(annotatedType.getType()))) {
            List<?> list = (List<?>) object;
            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    actuallyValidate(list.get(i),
                            ((AnnotatedParameterizedType)
                                    annotatedType).getAnnotatedActualTypeArguments()[0],
                            path + "[" + i + "]", errors);
                }
            }
        }
    }

    /***
     * Дополнительный метод для проверки того, является ли текущий объект сетом.
     * Если является, мы проверяем его элементы на соответствие указанным
     * параметрам-аннотациям. Путь формируем с добавлением квадратных
     * скобок и порядкового номера элемента в сете (важно кстати помнить, что
     * сеты не сохраняют порядок добавления элементов). Так реализуется поддержка
     * аннотаций как параметров сетов.
     * @param object - проверяемый объект.
     * @param annotatedType - тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @param errors - сет ошибок, которые мы собираем.
     */
    void checkSet(Object object, AnnotatedType annotatedType,
                  String path, Set<ValidationError> errors) {
        if (Set.class.isAssignableFrom(findClass(annotatedType.getType()))) {
            Set<?> set = (Set<?>) object;
            if (set != null) {
                List<?> list = set.stream().collect(Collectors.toList());
                for (int i = 0; i < list.size(); ++i) {
                    actuallyValidate(list.get(i),
                            ((AnnotatedParameterizedType)
                                    annotatedType).getAnnotatedActualTypeArguments()[0],
                            path + "[" + i + "]", errors);
                }
            }
        }
    }

    /***
     * Дополнительный метод для проверки того, является ли текущий объект мапой.
     * Если является, мы проверяем ее ключи и соответствующие им значения
     * на соответствие указанным параметрам-аннотациям. Путь формируем для ключей - с
     * добавлением поля массивов ключей и квадратных скобок с порядковым номером ключа
     * и для значений - как элемент мапы по индексу ключа. Так реализуется поддержка
     * аннотаций как параметров мап.
     * @param object - проверяемый объект.
     * @param annotatedType - тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @param errors - сет ошибок, которые мы собираем.
     */
    void checkMap(Object object, AnnotatedType annotatedType,
                  String path, Set<ValidationError> errors) {
        if (Map.class.isAssignableFrom(findClass(annotatedType.getType()))) {
            Map<?, ?> map = (Map<?, ?>) object;
            if (map != null) {
                List<?> keys = map.keySet().stream().collect(Collectors.toList());
                for (int i = 0; i < keys.size(); ++i) {
                    actuallyValidate(keys.get(i),
                            ((AnnotatedParameterizedType)
                                    annotatedType).getAnnotatedActualTypeArguments()[0],
                            path + ".keys[" + i + "]", errors);

                    actuallyValidate(map.get(keys.get(i)),
                            ((AnnotatedParameterizedType)
                                    annotatedType).getAnnotatedActualTypeArguments()[1],
                            path + "[\"" + keys.get(i) + "\"]", errors);
                }
            }
        }
    }

    /***
     * Метод где происходит распределение проверки. Если у объекта присутствует
     * какая-либо из аннотаций этой библиотеки, вызывается чекер для этой конкретной
     * аннотации. Далее вызываются методы проверки того, надо ли проверять объект вглубь
     * по его полям, является ли он коллекцией (надо ли проверять элементы).
     * Этот метод косвенно вызывается рекурсивно, обеспечивая тем самым обработку
     * вложенности как классов, так и коллекций.
     * @param object - проверяемый объект.
     * @param annotatedType - тип проверяемого объекта.
     * @param path - накопленный путь к объекту.
     * @param errors - сет ошибок, которые мы собираем.
     */
    void actuallyValidate(Object object, AnnotatedType annotatedType,
                          String path, Set<ValidationError> errors) {

        Class[] annotations = new Class[]{AnyOf.class, InRange.class,
                Negative.class, Positive.class, NotBlank.class, NotEmpty.class,
                NotNull.class, Size.class};

        for (Class annotation : annotations) {
            if (annotatedType.isAnnotationPresent(annotation)) {
                switchChecker(object, annotatedType, annotation.getSimpleName(), path, errors);
            }
        }

        checkConstrained(object, annotatedType, path, errors);
        checkList(object, annotatedType, path, errors);
        checkSet(object, annotatedType, path, errors);
        checkMap(object, annotatedType, path, errors);
    }

    /***
     * Метод с требуемой по заданию сигнатурой. Здесь мы создаем сет
     * ошибок, проверяем, что проверяемый объект имеет аннотацию для проверки
     * (если нет - бросаем исключение, потому что это некорректное использование библиотеки),
     * затем получаем поля объекта и, итерируясь по ним, вызываем метод проверки.
     * @param object - проверяемый объект.
     * @return - сет ошибок, найденных при проверке объекта на соответствие аннотациям.
     */
    @Override
    public Set<ValidationError> validate(Object object) {
        Set<ValidationError> errors = new HashSet<>();

        if (object.getClass().isAnnotationPresent(Constrained.class)) {

            Field[] fields = object.getClass().getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    actuallyValidate(field.get(object),
                            field.getAnnotatedType(), field.getName(), errors);
                } catch (IllegalAccessException e) {
                    //this should never actually happen
                    System.out.println("there was an illegal access " + e.getMessage());
                }
            }

        } else {
            throw new IllegalArgumentException("Non-constrained objects should not be validated");
        }

        return errors;
    }
}
