package validation;

import org.junit.jupiter.api.Test;
import weirdTestClasses.BookingForm;
import weirdTestClasses.GuestForm;
import weirdTestClasses.MultystoreyList;
import weirdTestClasses.Unrelated;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/***
 * Тест валидатора, здесь будут тестироваться глобальные примеры.
 */
class ActualValidatorTest {

    /***
     * Тест на то, что мы действительно не имеем права проверять
     * объекты, не отмеченные как constrained.
     */
    @Test
    void validateException() {
        ActualValidator validator = new ActualValidator();
        Unrelated unrelated = new Unrelated(-1);
        assertThrows(IllegalArgumentException.class, ()->validator.validate(unrelated));
    }

    /***
     * Тест того, что было в условии. Соответствие сетов проверить оказалось
     * сложнее, чем представлялось, поэтому проверяются стримами пути до ошибок
     * и сообщения об ошибках - будем считать, что совпасть столько раз просто
     * так оно не может (на самом деле рабочесть была проверена визуально,
     * но сделать это нормально действительно сложно, для этого нужно переопределять
     * равенство и сравнение для ошибок валидатора, что довольно странно делать просто
     * для тестов).
     */
    @Test
    void validateExample() {
        ActualValidator validator = new ActualValidator();
        List<GuestForm> guests = List.of(
                new GuestForm(
                /*firstName*/ null,
                /*lastName*/ "Def",
                /*age*/ 21),
                new GuestForm(
                        /*firstName*/ "",
                        /*lastName*/ "Ijk",
                        /*age*/ -3));
        Unrelated unrelated = new Unrelated(-1);
        BookingForm bookingForm = new BookingForm(guests,
                /*amenities*/ List.of("TV", "Piano"),
                /*propertyType*/ "Apartment", unrelated);
        Set<ValidationError> validationErrorsTest1 = validator.validate(bookingForm);

        Set<ValidationError> validationErrorsTest1Expected = new HashSet<>();

        validationErrorsTest1Expected.add(
                new ErrorNotNull(null, "guests[0].firstName"));
        validationErrorsTest1Expected.add(
                new ErrorNotBlank("", "guests[1].firstName"));
        validationErrorsTest1Expected.add(
                new ErrorAnyOf("Apartment", new String[]{"House", "Hostel"}, "propertyType"));
        validationErrorsTest1Expected.add(
                new ErrorAnyOf("Piano", new String[]{"TV", "Kitchen"}, "amenities[1]"));
        validationErrorsTest1Expected.add(
                new ErrorInRange(-3,0, 200, "guests[1].age"));

        assertEquals(validationErrorsTest1Expected.stream().map(
                ValidationError::getMessage).sorted().collect(Collectors.toList()),
                validationErrorsTest1.stream().map(
                        ValidationError::getMessage).sorted().collect(Collectors.toList()));

        assertEquals(validationErrorsTest1Expected.stream().map(
                ValidationError::getPath).sorted().collect(Collectors.toList()),
                validationErrorsTest1.stream().map(
                        ValidationError::getPath).sorted().collect(Collectors.toList()));
    }

    /***
     * Тест, в котором проверяются мои классы вложенных листов.
     */
    @Test
    void validateLists() {

        ActualValidator validator = new ActualValidator();

        MultystoreyList test = new MultystoreyList();
        Set<ValidationError> validationErrorsTest2 = validator.validate(test);
        Set<ValidationError> validationErrorsTest2Expected = new HashSet<>();

        validationErrorsTest2Expected.add(
                new ErrorNegative(1, "list2[0][0][0]"));
        validationErrorsTest2Expected.add(
                new ErrorNegative(0, "list2[0][0][2]"));
        validationErrorsTest2Expected.add(
                new ErrorPositive(0, "list2[0][0][2]"));
        validationErrorsTest2Expected.add(
                new ErrorPositive(-1, "list2[0][0][1]"));
        validationErrorsTest2Expected.add(
                new ErrorPositive(-1, "maps1[0][\"ш\"]"));
        validationErrorsTest2Expected.add(
                new ErrorPositive(0, "inside.set[0][0]"));
        validationErrorsTest2Expected.add(
                new ErrorPositive(0, "maps1[0][\"    \"]"));
        validationErrorsTest2Expected.add(
                new ErrorNotBlank("    ", "maps1[0].keys[3]"));
        validationErrorsTest2Expected.add(
                new ErrorNotBlank("   ", "inside.listStrings[1][1]"));
        validationErrorsTest2Expected.add(
                new ErrorNotBlank("", "inside.listStrings[0][0]"));
        validationErrorsTest2Expected.add(
                new ErrorNotEmpty("", "inside.listStrings[0][0]"));
        validationErrorsTest2Expected.add(
                new ErrorSize("", 3, 10,"inside.listStrings[0][0]"));
        validationErrorsTest2Expected.add(
                new ErrorSize("у", 3, 10, "inside.listStrings[0][2]"));
        validationErrorsTest2Expected.add(
                new ErrorSize("я", 3, 10, "inside.listStrings[0][1]"));
        validationErrorsTest2Expected.add(
                new ErrorSize("ыы", 3, 10, "inside.listStrings[1][3]"));
        validationErrorsTest2Expected.add(
                new ErrorSize("aaaaaaaaaaaa", 3, 10, "inside.listStrings[1][2]"));
        validationErrorsTest2Expected.add(
                new ErrorInRange(100, -1, 2, "inside.set[0][2]"));
        validationErrorsTest2Expected.add(
                new ErrorInRange(10, -2, 2, "maps1[0][\"э\"]"));
        validationErrorsTest2Expected.add(
                new ErrorNotNull(null, "list2[2]"));

       assertEquals(validationErrorsTest2Expected.size(), validationErrorsTest2.size());

        assertEquals(validationErrorsTest2Expected.stream().map(
                ValidationError::getMessage).sorted().collect(Collectors.toList()),
                validationErrorsTest2.stream().map(
                        ValidationError::getMessage).sorted().collect(Collectors.toList()));

        assertEquals(validationErrorsTest2Expected.stream().map(
                ValidationError::getPath).sorted().collect(Collectors.toList()),
                validationErrorsTest2.stream().map(
                        ValidationError::getPath).sorted().collect(Collectors.toList()));

    }

    /***
     * Тест метода поиска класса - класс действительно ищется нормально.
     */
    @Test
    void findClass() throws NoSuchFieldException {
        Unrelated test1 = new Unrelated("goodday");
        Field field = test1.getClass().getField("map");
        AnnotatedType annotatedType = field.getAnnotatedType();
        Type type = field.getType();
        assertEquals(field.getType(), (ActualValidator.findClass(annotatedType.getType())));
        assertEquals(field.getType(), (ActualValidator.findClass(type)));
    }
}