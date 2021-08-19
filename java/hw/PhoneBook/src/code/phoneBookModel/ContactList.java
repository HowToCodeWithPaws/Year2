package phoneBookModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/***
 * Класс для списка контактов. Здесь хранится коллекция контактов, есть методы
 * для добавления, изменения и удаления контактов, записи и чтения из файлов.
 * Класс сериализуется нативной сериализацией джавы.
 */
public class ContactList implements Serializable {
    /***
     * Наблюдаемая коллекция контактов - так мы будем хранить наши контакты.
     */
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    /***
     * Дефолтный конструктор для сериализации. Ничего особенно не делает.
     */
    public ContactList() {
    }

    /***
     * Конструктор с параметром коллекции - мы проверяем каждый контакт на корректность,
     * и добавляем его, если все хорошо.
     * @param contacts_ коллекция, на основании которой мы формируем список.
     */
    public ContactList(List<Contact> contacts_) {
        if (contacts_ != null) {
            for (Contact contact : contacts_) {
                if (Contact.checkContact(contact) && !contacts.contains(contact)) {
                    contacts.add(contact);
                }
            }
        }
    }

    /***
     * Геттер для коллекции. Нужен вообще для того, чтобы привязывать таблицу к данным.
     * @return наблюдаемая коллекция контактов.
     */
    public ObservableList getCollection() {
        return contacts;
    }

    /***
     * Метод для добавления контакта. Мы добавляем контакт, если его еще нет
     * в коллекции и если он проходит проверку корректности.
     * @param contact добавляемый контакт.
     * @return правда, если мы добавили контакт, и ложь, если он не подошел.
     */
    public boolean addContact(Contact contact) {
        if (contacts.contains(contact) || !Contact.checkContact(contact)) {
            return false;
        }
        contacts.add(contact);
        return true;
    }

    /***
     * Метод удаления контакта. Мы тут особенно ничего не проверяем,
     * потому что в коде он вызывается только при выбранном контакте,
     * то есть он гарантированно есть в коллекции.
     * @param contact контакт, который мы удаляем.
     */
    public void deleteContact(Contact contact) {
        contacts.remove(contact);
    }

    /***
     * Метод для изменения контакта. Менять контакт, изменяя значения полей по
     * отдельности не очень удобно, потому что некоторые части корректности
     * зависят от комбинации нескольких полей сразу, так что для изменения мы будем
     * создавать новый контакт и заменять им старый целиком. Нельзя менять контакт на
     * другой уже существующий (тогда будет дублирование).
     * @param oldvalue старое значение меняемого контакта.
     * @param newvalue новое значение.
     * @return правда, если мы поменяли контакт, ложь иначе.
     */
    public boolean editContact(Contact oldvalue, Contact newvalue) {
        if (contacts.contains(newvalue) && contacts.indexOf(newvalue) != contacts.indexOf(oldvalue) || !Contact.checkContact(newvalue)) {
            return false;
        }
        contacts.set(contacts.indexOf(oldvalue), newvalue);
        return true;
    }

    /***
     * Метод добавления другого списка контактов - нужно для импорта,
     * при этом добавляются все корректные контакты, которых еще нет, а
     * про другие добавляются ошибки в коллекцию ошибок, которую мы будем
     * возвращать.
     * @param newContacts добавляемый список.
     * @return коллекция ошибок, возникших при импортировании контактов.
     */
    public ArrayList<String> addContacts(ContactList newContacts) {
        ArrayList<String> errors = new ArrayList<>();
        if (newContacts != null) {
            for (Object element : newContacts.getCollection()) {
                Contact contact = (Contact) element;
                if (contact != null) {
                    if (Contact.checkContact(contact)) {
                        if (!this.addContact(contact))
                            errors.add("такой контакт уже существует:\n" + contact.toString());
                    } else errors.add("контакт заполнен неверно:\n" + contact.toString());
                } else errors.add("контакт пустой");///this should literally never happen but still
            }
        } else errors.add("коллекция контактов пуста");
        return errors;
    }

    /***
     * Статический метод для записи в файл - сериализация списка контактов по указанному адресу.
     * @param path адрес файла.
     * @param contactList сериализуемый список контактов.
     * @throws IOException может выбрасывать исключения записи, которые мы будем ловить в точке вызова.
     */
    public static void writeToFile(String path, ContactList contactList) throws IOException {
        File fw = new File(path);
        ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(fw));
        ostream.writeObject(new ArrayList<Contact>(contactList.getCollection()));
        ostream.close();
    }

    /***
     * Статический метод для чтения из файла - десериализация списка контактов по указанному адресу.
     * @param path адрес файла.
     * @return десериализованный список.
     * @throws IOException может выбрасывать исключения чтения, которые мы будем ловить в точке вызова.
     * @throws ClassNotFoundException может выбрасывать исключение при приведении к классу, будем ловить выше.
     */
    public static ContactList readFromFile(String path) throws IOException, ClassNotFoundException {
        File fr = new File(path);
        ObjectInputStream istream = new ObjectInputStream(new FileInputStream(fr));
        ContactList contactList = new ContactList((ArrayList<Contact>) istream.readObject());
        istream.close();
        return contactList;
    }
}
