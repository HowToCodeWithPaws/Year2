package bdStuff;

import org.junit.jupiter.api.Test;
import phoneBookModel.Contact;
import phoneBookModel.ContactList;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DerbyThingsTest {

    /***
     * с этими тестами беды, потому что перед некоторыми надо удалять таблицы
     * с которыми они работают (потому что они например заполняются и тест на пустоту
     * не проходит), а перед другими например наоборот
     * периодически еще если запускать их параллельно с другими,
     * они выкидывают довольно страшные эксепшены
     * про невозможность открыть диалоговое окно
     * поскольку никто вообще не говорил, что тесты к бд нужны,
     * я предположу, что в принципе работающие со скрипом тесты это лучше
     * чем их отсутствие
     * короче, пожалуйста, не бейте, если что-то идет с ними не так,
     * честное слово они работают
     * @throws SQLException
     */
    @Test
    void makeDB() throws SQLException {
        String name = "testDB1";
        Connection connection = DriverManager.getConnection("jdbc:derby:" + name + ";create=true");
        Statement statement = connection.createStatement();
        assertThrows(SQLException.class, () -> {
            ResultSet contactsFromDB = statement.executeQuery("select * from CONTACTS");
        });
        connection.close();
        statement.close();
        connection = DerbyThings.makeDB("testDB1");
        Statement statement2 = connection.createStatement();
        assertDoesNotThrow(() -> {
            ResultSet contactsFromDB = statement2.executeQuery("select * from CONTACTS");
        });

        assertFalse(statement2.executeQuery("select * from CONTACTS").next());
        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        connection.close();
        statement2.close();
    }

    @Test
    void getFromDB() throws SQLException {
        Connection connection = DerbyThings.makeDB("testDB2");
        assertDoesNotThrow(() -> {
            DerbyThings.getFromDB(connection);
        });

        assertTrue(DerbyThings.getFromDB(connection).getCollection().isEmpty());

        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        connection.close();
    }

    @Test
    void deleteFromDB() throws SQLException {
        Connection connection = DerbyThings.makeDB("testDBNotEmpty3");
        ContactList contactList = new ContactList();
        Contact contact = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        contact.setId(1);
        assertDoesNotThrow(() -> DerbyThings.deleteFromDB(contact, connection));

        contactList.addContact(contact);
        DerbyThings.writeToDB(connection, contactList);

        DerbyThings.deleteFromDB(contact, connection);

        assertTrue(DerbyThings.getFromDB(connection).getCollection().isEmpty());
        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        connection.close();
    }

    @Test
    void writeToDB() throws SQLException {
        ContactList contactList = new ContactList();
        contactList.addContact(new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05"));
        Connection connection = DerbyThings.makeDB("testDBNotEmpty4");

        DerbyThings.writeToDB(connection, contactList);

        assertFalse(DerbyThings.getFromDB(connection).getCollection().isEmpty());
        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        connection.close();
    }

    @Test
    void insertInBd() throws SQLException {
        Contact contact = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "8-495-251-31-51",
                "что-то где-то", "примерный человек", "2001-04-05");
        Connection connection = DerbyThings.makeDB("testDBNotEmpty5");

        DerbyThings.insertInBd(contact, connection);

        assertFalse(DerbyThings.getFromDB(connection).getCollection().isEmpty());
        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        connection.close();
    }

    @Test
    void updateBD() throws SQLException {
        Contact contact = new Contact("Фамилия", "Имя", "Отчество",
                "+7 916 250 80 66", "84952513151",
                "что-то где-то", "примерный человек", "20010405");
        Connection connection = DerbyThings.makeDB("testDBNotEmpty6");
        contact.setId(5);

        assertDoesNotThrow(() ->
                DerbyThings.updateBD(contact, connection));
        DerbyThings.insertInBd(contact, connection);

        assertDoesNotThrow(() ->
                DerbyThings.updateBD(contact, connection));
        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        connection.close();
    }

    @Test
    void closeDB() throws SQLException {
        String name = "testDB7";
        Connection connection = DerbyThings.makeDB(name);
        Statement statement2 = connection.createStatement();
        assertDoesNotThrow(() -> {
            ResultSet contactsFromDB = statement2.executeQuery("select * from CONTACTS");
        });

        DerbyThings.closeDB(connection, "org.apache.derby.jdbc.EmbeddedDriver");
        assertThrows(SQLException.class, () -> {
            ResultSet contactsFromDB = statement2.executeQuery("select * from CONTACTS");
        });
        connection.close();
        statement2.close();
    }
}