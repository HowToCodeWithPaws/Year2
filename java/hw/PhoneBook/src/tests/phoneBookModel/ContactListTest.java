package phoneBookModel;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ContactListTest {

    @Test
    void getCollection() {
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        ContactList contactList = new ContactList(Arrays.asList(contact1, contact2));
        assertEquals(2, contactList.getCollection().size());
        assertEquals(contact1, contactList.getCollection().get(0));
        assertEquals(contact2, contactList.getCollection().get(1));
    }

    @Test
    void addContact() {
        ContactList contactList = new ContactList();
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "", "2001-04-05");
        Contact contact3 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact4 = new Contact("", "Имя", "Отчество",
                "", "",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact5 = new Contact("Фамилия", "Имя", "Отчество",
                "", "",
                "давным давно в далекой-далекой галактике", "тестируемся", "2021-04-05");
        assertTrue(contactList.addContact(contact1));
        assertTrue(contactList.addContact(contact2));
        assertFalse(contactList.addContact(contact3));
        assertFalse(contactList.addContact(contact4));
        assertFalse(contactList.addContact(contact5));
        assertEquals(2,contactList.getCollection().size());
    }

    @Test
    void deleteContact() {
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "", "2001-04-05");
        ContactList contactList = new ContactList(Arrays.asList(contact1, contact2));
        assertEquals(2,contactList.getCollection().size());
        contactList.deleteContact(contact1);
        assertEquals(1,contactList.getCollection().size());
        assertEquals(contact2, contactList.getCollection().get(0));
       assertDoesNotThrow(()->contactList.deleteContact(contact1));
    }

    @Test
    void editContact() {
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "", "2001-04-05");
        Contact contact3 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact4 = new Contact("Фамилия", "Имя Поменяли", "Отчество",
                "", "",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        ContactList contactList = new ContactList(Arrays.asList(contact1, contact2));

        assertTrue(contactList.editContact(contact1, contact3));
        assertEquals(contact3.toString(), contactList.getCollection().get(0).toString());
        assertFalse(contactList.editContact(contact2, contact3));
        assertFalse(contactList.editContact(contact2, contact4));
        assertEquals(2,contactList.getCollection().size());
    }

    @Test
    void writeToFile() {
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        ContactList contactList = new ContactList(Arrays.asList(contact1, contact2));
        assertDoesNotThrow(() -> ContactList.writeToFile("serialization.dat", contactList));

        try {
            File fr = new File("serialization.dat");
            ObjectInputStream istream = new ObjectInputStream(new FileInputStream(fr));
            ContactList contactList1 = new ContactList((ArrayList<Contact>) istream.readObject());
            istream.close();

            for (int i = 0; i <2;++i              ) {
                assertEquals(contactList.getCollection().get(i), contactList1.getCollection().get(i));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void readFromFile() {
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
            "89162508063", "84990000000",
            "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        ContactList contactList = new ContactList(Arrays.asList(contact1, contact2));

        try {
            File fw = new File("serialization.dat");
            ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(fw));
            ostream.writeObject(new ArrayList<Contact>(contactList.getCollection()));
            ostream.close();

            AtomicReference<ContactList> contactList1 = new AtomicReference<>(new ContactList());

            assertDoesNotThrow(() -> contactList1.set(ContactList.readFromFile("serialization.dat")));

            for (int i = 0; i <2;++i              ) {
                assertEquals(contactList.getCollection().get(i), contactList1.get().getCollection().get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addContacts() {
        Contact contact1 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "", "тестируемся", "2001-04-05");
        Contact contact2 = new Contact("Фамилия", "Другое Имя", "",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "", "2001-04-05");
        Contact contact3 = new Contact("Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact4 = new Contact("", "Имя", "Отчество",
                "", "",
                "давным давно в далекой-далекой галактике", "тестируемся", "2001-04-05");
        Contact contact5 = new Contact("Фамилия", "Имя", "Отчество",
                "", "",
                "давным давно в далекой-далекой галактике", "тестируемся", "2021-04-05");
        Contact contact6 = null;
        Contact contact7 =  new Contact("Другая Фамилия", "Имя", "Отчество",
                "89162508063", "84990000000",
                "", "тестируемся", "2001-04-05");

        ContactList contactList = new ContactList(Arrays.asList(contact1, contact2));
        ContactList contactList1 = null;
        assertEquals("коллекция контактов пуста", contactList.addContacts(contactList1).get(0));
        ContactList contactList2 = new ContactList(Arrays.asList(contact3, contact4, contact5, contact6, contact7));
        contactList2.addContact(contact3);
        contactList2.addContact(contact4);
        contactList2.addContact(contact5);
        contactList2.addContact(contact6);
        contactList2.addContact(contact7);
        assertEquals(2, contactList2.getCollection().size());
        ArrayList<String> errors = new ArrayList<String>(Arrays.asList(new String[]{"такой контакт уже существует:\n" + contact3.toString()}));
        assertEquals(errors, contactList.addContacts(contactList2));
        assertEquals(3,contactList.getCollection().size());
    }
}