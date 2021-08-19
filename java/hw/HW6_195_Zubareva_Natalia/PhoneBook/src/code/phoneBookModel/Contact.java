package phoneBookModel;

import java.io.Serializable;
import java.time.LocalDate;

/***
 * Класс для контакта - он хранит строковые свойства контакта,
 * соответствующие всем, указанным в условии. Есть методы для
 * проверки корректности потенциальных значений таких полей,
 * еще этот класс умеет сериализоваться.
 * тут нигде нет сеттеров для полей, потому что они не нужны
 * при той схеме редактирования контакта, которая у меня реализована
 * не бейте
 */
public class Contact implements Serializable {

    /***
     * Поля для ФИО, номеров телефонов, адрес, день рождения и заметки.
     */
    private final String lastName, firstName, patronymic,
            cellPhoneNumber, landlinePhoneNumber,
            address, notes, birthday;

    /***
     * Поле для идентификатора, дефолтно равно -1.
     */
    private Integer id = -1;

    /***
     * Регексы для допустимых форматов номеров телефона (через пробелы или дефисы(я не знаю зачем)),
     * имя - допускаются тройные имена, фамилии и отчества, суммарная длина букв в каждом до 30.
     */
    private static final String numberWithDashes =
            "^((\\+7|7|8)+(\\-?)(([0-9]){3})(\\-?)+([0-9]){3}(\\-?)+([0-9]){2}(\\-?)([0-9]){2})$";
    private static final String numberWithSpaces =
            "^((\\+7|7|8)+( ?)(([0-9]){3})( ?)+([0-9]){3}( ?)+([0-9]){2}( ?)([0-9]){2})$";
    private static final String name =
            "^([A-Za-z]|[А-Яа-я]){0,10}( ?)([A-Za-z]|[А-Яа-я]){0,10}( ?)([A-Za-z]|[А-Яа-я]){0,10}$";

    /***
     * Конструктор контакта с ожидаемыми параметрами.
     * @param lastName_ фамилия.
     * @param firstName_ имя.
     * @param patronymic_ отчество.
     * @param cellPhoneNumber_ мобильный.
     * @param landlinePhoneNumber_ домашний.
     * @param address_ адрес.
     * @param notes_ заметки.
     * @param birthday_ день рождения.
     */
    public Contact(String lastName_, String firstName_, String patronymic_,
                   String cellPhoneNumber_, String landlinePhoneNumber_,
                   String address_, String notes_, String birthday_) {
        lastName = lastName_;
        firstName = firstName_;
        patronymic = patronymic_;
        cellPhoneNumber = cellPhoneNumber_;
        landlinePhoneNumber = landlinePhoneNumber_;
        address = address_;
        notes = notes_;
        birthday = birthday_;
        id = -1;
    }

    /***
     * Конструктор без параметров для того, чтобы сериализовалось. Все поля дефолтно
     * становятся пустыми строками.
     */
    public Contact() {
        firstName = lastName = patronymic = cellPhoneNumber
                = landlinePhoneNumber = address = notes = birthday = "";
    }

    /***
     * Метод для проверки корректности заполнения контакта:
     * контакт должен быть ненулевым, у него должны быть фамилия и имя (запретили маму :с)
     * и хотя бы одини из номеров телефона. Также фио должны соответствовать формату имен,
     * телефоны - телефонов, а дата рождения не должна быть из будущего.
     * @param newContact контакт, который проверяется на корректность.
     * @return возвращается булевое значение правды, если контакт корректно заполнен и ложь иначе.
     */
    public static boolean checkContact(Contact newContact) {
        return newContact != null && checkName(newContact.firstName)
                && checkName(newContact.lastName)
                && (newContact.patronymic.isEmpty() || checkName(newContact.patronymic))
                && !(newContact.cellPhoneNumber.isBlank()
                && newContact.landlinePhoneNumber.isBlank())
                && checkPhoneNumber(newContact.cellPhoneNumber)
                && checkPhoneNumber(newContact.landlinePhoneNumber)
                && checkBirthday(newContact.getBirthday());
    }

    /***
     * Метод для проверки имени - оно не должно быть пустым и должно соответствовать формату.
     * @param name_ проверяемая строка - какая-то часть фио.
     * @return булевое значение правды, если имя корректно, ложь иначе.
     */
    public static boolean checkName(String name_) {
        return !name_.isBlank() && name_.matches(name);
    }

    /***
     * Метод для проверки номера телефона - он либо пустой, либо соответствует одному
     * из форматов номеров - через пробел или дефис.
     * @param phoneNumber_ проверяемый номер телефона.
     * @return правда, если номер корректный, ложь иначе.
     */
    public static boolean checkPhoneNumber(String phoneNumber_) {
        return phoneNumber_.isBlank() || phoneNumber_.matches(numberWithDashes)
                || phoneNumber_.matches(numberWithSpaces);
    }

    /***
     * Метод проверки того, что дата рождения правильная - либо пустая, либо не из будущего.
     * @param birthday_ проверяемая дата рождения.
     * @return правда, если дата корректна, ложь иначе.
     */
    public static boolean checkBirthday(String birthday_) {
        return birthday_.isEmpty() || LocalDate.parse(birthday_).compareTo(LocalDate.now()) <= 0;
    }

    /***
     * Геттер для даты рождения.
     * @return дата рождения.
     */
    public String getBirthday() {
        return birthday;
    }

    /***
     * Геттер для фамилии.
     * @return фамилия.
     */
    public String getLastName() {
        return lastName;
    }

    /***
     * Геттер для имени.
     * @return имя.
     */
    public String getFirstName() {
        return firstName;
    }

    /***
     * Геттер для отчества.
     * @return отчество.
     */
    public String getPatronymic() {
        return patronymic;
    }

    /***
     * Геттер для полного ФИО разделенного пробелами.
     * @return ФИО.
     */
    public String getFullName() {
        return getLastName() + " " + getFirstName() + " " + getPatronymic();
    }

    /***
     * Геттер для номера мобильного телефона.
     * @return мобильный.
     */
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    /***
     * Геттер для домашнего телефона.
     * @return домашний.
     */
    public String getLandlinePhoneNumber() {
        return landlinePhoneNumber;
    }

    /***
     * Геттер для адреса.
     * @return адрес.
     */
    public String getAddress() {
        return address;
    }

    /***
     * Геттер для заметок.
     * @return заметки.
     */
    public String getNotes() {
        return notes;
    }

    /***
     * Геттер для идентификатора контакта.
     * @return идентификатор контакта - у нового контакта он -1, у
     * того, который уже был в базе данных - сгенерированный там.
     */
    public Integer getId() {
        return id;
    }

    /***
     * Сеттер для идентификатора контакта.
     * @param id_ - новое значение идентификатора, генерируется при записи в бд.
     */
    public void setId(int id_) {
        id = id_;
    }

    /***
     * Переопределенный метод сравнения контакта на равенство с другим объектом.
     * Мы считаем равными контакты, у которых одинаковые ФИО.
     * @param other объект, с которым контакт сравнивается на равенство.
     * @return правда, если другой объект - тоже класса контакт, и его ФИО равно ФИО нашего контакта.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Contact && ((Contact) other).firstName.equals(this.firstName)
                && ((Contact) other).lastName.equals(this.lastName)
                && ((Contact) other).patronymic.equals(this.patronymic);
    }

    /***
     * Переопределенный метод хешкода, потому что переопределять равенство, но не
     * переопределять хеш - нехорошо.
     * @return значение хешфункции, формируемое по ФИО.
     */
    @Override
    public int hashCode() {
        return (lastName + firstName + patronymic).hashCode();
    }

    /***
     * Переопределенный метод приведения к строке. Строка контакта представляет
     * собой информацию обо всех его параметрах.
     * @return строка с информацией всех полей контакта.
     */
    @Override
    public String toString() {
        return "ФИО: " + getFullName() + "\nМобильный: " + getCellPhoneNumber() +
                "\nДомашний: " + getLandlinePhoneNumber() + "\nАдрес: " + getAddress() +
                "\nДень рождения: " + getBirthday() + "\nЗаметки: " + getNotes();
    }
}
