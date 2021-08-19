package bdStuff;

import phoneBookModel.Contact;
import phoneBookModel.ContactList;
import phoneBookUI.MainScreen;

import java.sql.*;

public class DerbyThings {

    /***
     *   Метод, в котором происходит подключение к дерби, там создается таблица.
     * @param name - имя таблицы, которую будем создавать.
     * @return - возвращает соединение с базой данных.
     */
    public static Connection makeDB(String name) {
        Connection connection = null;
        try {
            String createString = "CREATE TABLE CONTACTS  "
                    + "(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
                    + " FIRSTNAME VARCHAR(40) NOT NULL, "
                    + " LASTNAME VARCHAR(40) NOT NULL, "
                    + " PATRONYMIC VARCHAR(40), "
                    + " CELLPHONE VARCHAR(20), "
                    + " LANDLINEPHONE VARCHAR(20), "
                    + " ADDRESS VARCHAR(255), "
                    + " BIRTHDAY VARCHAR(15), "
                    + " NOTES VARCHAR(255), "
                    + "PRIMARY KEY (ID, FIRSTNAME, LASTNAME, PATRONYMIC)) ";

            connection = DriverManager.getConnection("jdbc:derby:" + name + ";create=true");
            Statement statement = connection.createStatement();
            statement.execute(createString);
            statement.close();

        } catch (SQLException se) {
            if (!se.getSQLState().equals("X0Y32")) {
                MainScreen.errorShow("Подключение к базе данных",
                        "База данных с контактами", "При создании базы данных" +
                                " произошла ошибка! Попробуйте снова.");
            }
        } catch (Throwable e) {
            MainScreen.errorShow("Подключение к базе данных",
                    "База данных с контактами", "При подключении" +
                            " произошла ошибка! Вам придется создавать контакты заново.");
        }
        return connection;
    }

    /***
     * Метод для получения контактов - если приложение уже запускалось, то контакты
     * были записаны в базу данных, откуда мы получаем их в начале каждой сесии. Если
     * бд нет или она сломана, выводим предупреждение.
     * @param connection - подключение к серверу. Вообще этот параметр нужен только для тестов.
     */
    public static ContactList getFromDB(Connection connection) {
        ContactList contacts = new ContactList();
        try {
            ResultSet contactsFromDB;
            Statement statement = connection.createStatement();
            contactsFromDB = statement.executeQuery("select * from CONTACTS");
            while (contactsFromDB.next()) {
                Contact newContact = new Contact(
                        contactsFromDB.getString("LASTNAME"),
                        contactsFromDB.getString("FIRSTNAME"),
                        contactsFromDB.getString("PATRONYMIC"),
                        contactsFromDB.getString("CELLPHONE"),
                        contactsFromDB.getString("LANDLINEPHONE"),
                        contactsFromDB.getString("ADDRESS"),
                        contactsFromDB.getString("NOTES"),
                        contactsFromDB.getString("BIRTHDAY"));
                newContact.setId(contactsFromDB.getInt("ID"));
                contacts.addContact(newContact);
            }
            contactsFromDB.close();
        } catch (Exception e) {
            MainScreen.errorShow("Загрузка сохраненных данных",
                    "База данных с контактами", "При загрузке созданных ранее контактов" +
                            " произошла ошибка! Вам придется создавать контакты заново.");
        }
        return contacts;
    }

    /***
     * Метод, в котором мы сразу при удалении контакта удаляем его из базы данных тоже.
     * @param contact - контакт, который мы хотим удалить.
     * @param connection - подключение к серверу. Вообще этот параметр нужен только для тестов.
     */
    public static void deleteFromDB(Contact contact, Connection connection) {
        try {
            PreparedStatement psDelete;
            psDelete = connection.prepareStatement("DELETE FROM CONTACTS WHERE ID = ?");
            psDelete.setString(1, contact.getId().toString());
            psDelete.executeUpdate();
            psDelete.close();
        } catch (Throwable e) {
            MainScreen.errorShow("Удаление данных",
                    "База данных с контактами", "При удалении контакта произошла ошибка. Попробуйте снова.");
        }
    }

    /***
     * Метод для записи контактов в базу данных - каждый контакт, в соответствии с своим идентификатором,
     * либо добавляется (если контакт новый) в бд, либо обновляется.
     * @param connection - подключение к серверу. Вообще этот параметр нужен только для тестов.
     * @param contacts - список контактов, с которыми мы работали.
     * @throws SQLException - возможные исключения при работе с бд обрабатываются в точке вызова.
     */
    public static void writeToDB(Connection connection, ContactList contacts) throws SQLException {
        for (Object contact : contacts.getCollection()) {
            Contact contactAsContact = (Contact) contact;
            if (contactAsContact.getId() == -1) {
                insertInBd((Contact) contact, connection);
            } else {
                updateBD((Contact) contact, connection);
            }
        }
    }

    /***
     * Метод для записи в бд созданных контактов.
     * @param contact - контакт, который мы создали за последний сеанс работы.
     * @param connection - подключение к серверу. Вообще этот параметр нужен только для тестов.
     * @throws SQLException - возможные исключения при работе с бд обрабатываются в точке вызова.
     */
    public static void insertInBd(Contact contact, Connection connection) throws SQLException {
        PreparedStatement psInsert;
        psInsert = connection.prepareStatement("INSERT INTO CONTACTS(LASTNAME, FIRSTNAME, PATRONYMIC, " +
                "CELLPHONE, LANDLINEPHONE, ADDRESS, BIRTHDAY, NOTES)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        psInsert.setString(1, contact.getLastName());
        psInsert.setString(2, contact.getFirstName());
        psInsert.setString(3, contact.getPatronymic());
        psInsert.setString(4, contact.getCellPhoneNumber());
        psInsert.setString(5, contact.getLandlinePhoneNumber());
        psInsert.setString(6, contact.getAddress());
        psInsert.setString(7, contact.getBirthday());
        psInsert.setString(8, contact.getNotes());
        psInsert.executeUpdate();
        psInsert.close();
    }

    /***
     * Метод для обновления данных о существующем контакте по его идентификатору.
     * @param contact - контакт, который мы обновляем.
     * @param connection - подключение к серверу. Вообще этот параметр нужен только для тестов.
     * @throws SQLException - возможные исключения при работе с бд обрабатываются в точке вызова.
     */
    public static void updateBD(Contact contact, Connection connection) throws SQLException {
        PreparedStatement psUpdate;
        psUpdate = connection.prepareStatement("UPDATE CONTACTS SET LASTNAME = ?, FIRSTNAME = ?, PATRONYMIC = ?," +
                " CELLPHONE = ?, LANDLINEPHONE = ?, ADDRESS = ?, BIRTHDAY = ?, NOTES = ? WHERE ID = ?");

        psUpdate.setString(1, contact.getLastName());
        psUpdate.setString(2, contact.getFirstName());
        psUpdate.setString(3, contact.getPatronymic());
        psUpdate.setString(4, contact.getCellPhoneNumber());
        psUpdate.setString(5, contact.getLandlinePhoneNumber());
        psUpdate.setString(6, contact.getAddress());
        psUpdate.setString(7, contact.getBirthday());
        psUpdate.setString(8, contact.getNotes());
        psUpdate.setString(9, contact.getId().toString());
        psUpdate.executeUpdate();
        psUpdate.close();
    }

    /***
     * Метод для закрытия всех использованных ресурсов бд.
     * @param connection - подключение к серверу. Вообще этот параметр нужен только для тестов.
     * @param driver - название драйвера, с которым закрываем подключение.
     * @throws SQLException - возможные исключения при работе с бд обрабатываются в точке вызова.
     */
    public static void closeDB(Connection connection, String driver) throws SQLException {
        connection.close();
        if (driver.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
            boolean gotSQLExc = false;
            Connection newConnection = null;
            try {
                newConnection = DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException se) {
                if (se.getSQLState().equals("XJ015")) {
                    gotSQLExc = true;
                }
            }

            if (!gotSQLExc) {
                newConnection.close();
                MainScreen.errorShow("Завершение работы", "База данных",
                        "Не получилось закрыть подключение к базе данных. Попробуйте снова.");
            }
        }
    }
}
