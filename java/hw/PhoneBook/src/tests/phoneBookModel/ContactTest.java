package phoneBookModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void checkContact() {
        Contact contact0 = null;
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        Contact contact3 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "",
                "что-то где-то", "примерный человек", "2001-04-05");
        Contact contact4 = new Contact("Фамилия", "Имя", "Отчество",
                "", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        Contact contact5 = new Contact("Фамилия", "Имя", "Отчество",
                "", "",
                "", "второй примерный человек", "2001-04-05");
        Contact contact6 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2022-05-04");
        assertFalse(Contact.checkContact(contact0));
        assertFalse(Contact.checkContact(contact1));
        assertTrue(Contact.checkContact(contact2));
        assertTrue(Contact.checkContact(contact3));
        assertTrue(Contact.checkContact(contact4));
        assertFalse(Contact.checkContact(contact5));
        assertFalse(Contact.checkContact(contact6));
    }

    @Test
    void checkName() {
        String name1 = "334";
        String name2 = "";
        String name3 = "###";
        String name4 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String name5 = "нормальное имя";
        String name6 = "Тоже нормальное имя";
        String name7 = "Но это уже слишком";
        assertFalse(Contact.checkName(name1));
        assertFalse(Contact.checkName(name2));
        assertFalse(Contact.checkName(name3));
        assertFalse(Contact.checkName(name4));
        assertTrue(Contact.checkName(name5));
        assertTrue(Contact.checkName(name6));
        assertFalse(Contact.checkName(name7));
    }

    @Test
    void checkPhoneNumber() {
        String number1 = "8888888888888888";
        String number2 = "   ";
        String number3 = "hgsf";
        String number4 = "11";
        String number5 = "+7 891 232 23 23";
        String number6 = "8-982-342-23-34";
        String number7 = "73737877878";
        assertTrue(Contact.checkPhoneNumber(number1));
        assertTrue(Contact.checkPhoneNumber(number2));
        assertFalse(Contact.checkPhoneNumber(number3));
        assertFalse(Contact.checkPhoneNumber(number4));
        assertTrue(Contact.checkPhoneNumber(number5));
        assertTrue(Contact.checkPhoneNumber(number6));
        assertTrue(Contact.checkPhoneNumber(number7));
    }

    @Test
    void checkBirthday() {
        String birthday1 = "";
        String birthday2 = "2020-01-01";
        String birthday3 = "2021 01 01";
        String birthday4 = "2021/03/01";
        String birthday5 = "2022-05-04";
        String birthday6 = "ssss";
        String birthday7 = "20200202";
        assertTrue(Contact.checkBirthday(birthday1));
        assertTrue(Contact.checkBirthday(birthday2));
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> Contact.checkBirthday(birthday3));
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> Contact.checkBirthday(birthday4));
        assertFalse(Contact.checkBirthday(birthday5));
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> Contact.checkBirthday(birthday6));
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> Contact.checkBirthday(birthday7));
    }

    @Test
    void getBirthday() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getBirthday());
        assertEquals("2001-04-05", contact2.getBirthday());
    }

    @Test
    void getLastName() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getLastName());
        assertEquals("Фамилия", contact2.getLastName());
    }

    @Test
    void getFirstName() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getFirstName());
        assertEquals("Имя", contact2.getFirstName());
    }

    @Test
    void getPatronymic() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getPatronymic());
        assertEquals("Отчество", contact2.getPatronymic());
    }

    @Test
    void getFullName() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("  ", contact1.getFullName());
        assertEquals("Фамилия Имя Отчество", contact2.getFullName());
    }

    @Test
    void getCellPhoneNumber() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getCellPhoneNumber());
        assertEquals("+7 916 250 80 66", contact2.getCellPhoneNumber());
    }

    @Test
    void getLandlinePhoneNumber() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getLandlinePhoneNumber());
        assertEquals("8-495-251-31-51", contact2.getLandlinePhoneNumber());
    }

    @Test
    void getAddress() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getAddress());
        assertEquals("что-то где-то", contact2.getAddress());
    }

    @Test
    void getNotes() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("", contact1.getNotes());
        assertEquals("примерный человек", contact2.getNotes());
    }

    @Test
    void testEquals() {
        Object object = null;
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        Contact contact3 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "",
                "", "второй примерный человек", "2001-04-05");
        assertNotEquals(object, contact1);
        assertNotEquals(contact2, contact1);
        assertEquals(contact2, contact3);
    }

    @Test
    void testHashCode() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("".hashCode(), contact1.hashCode());
        assertEquals("ФамилияИмяОтчество".hashCode(), contact2.hashCode());
    }

    @Test
    void testToString() {
        Contact contact1 = new Contact();
        Contact contact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals("ФИО:   \nМобильный: \nДомашний: \nАдрес: \nДень рождения: " +
                "\nЗаметки: ", contact1.toString());
        assertEquals("ФИО: Фамилия Имя Отчество\nМобильный: +7 916 250 80 66" +
                "\nДомашний: 8-495-251-31-51\nАдрес: что-то где-то\nДень рождения: 2001-04-05" +
                "\nЗаметки: примерный человек", contact2.toString());
    }

    @Test
    void getId() {
        Contact newContact1 = new Contact();
        Contact newContact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        assertEquals(-1, newContact1.getId());
        assertEquals(-1, newContact2.getId());
    }

    @Test
    void setId() {
        Contact newContact1 = new Contact();
        Contact newContact2 = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        newContact1.setId(0);
        newContact2.setId(2);
        assertEquals(0, newContact1.getId());
        assertEquals(2, newContact2.getId());
    }
}