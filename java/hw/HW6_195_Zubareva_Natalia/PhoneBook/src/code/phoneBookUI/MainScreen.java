package phoneBookUI;

import bdStuff.DerbyThings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import phoneBookModel.Contact;
import phoneBookModel.ContactList;

import java.sql.*;
import java.util.Arrays;

/***
 * Класс для основного экрана. Он состоит их таблицы контактов, верхнего меню и нижней
 * панели с кнопками и строкой поиска.
 */
public class MainScreen extends Application {
    ContactList contacts = new ContactList();
    FilteredList<Contact> filteredContacts;
    TableView<Contact> table;
    MenuBar topMenuBar = new MenuBar();
    HBox bottomMenuBar;
    Menu fileMenu, settingsMenu, infoMenu;
    MenuItem addOpt, editOpt, deleteOpt, exitOpt, importOpt, exportOpt, aboutAuthorOpt, aboutAppOpt;
    Button addB, editB, deleteB, searchB;
    TextField searchField;
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    Connection thisConnection;
    /***
     * Константные статические значения для страшной красной рамки неправильных данных
     * и дефолтной рамки.
     */
    public static Border scaryRed = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    public static Border defaultBlack = new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.DEFAULT));


    /***
     * Метод для запуска.
     * @param args параметры запуска приложения.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /***
     * Метод для запуска приложения - вызывает создание базы данных,
     * получение контактов оттуда, метод настройки элементов экрана.
     * @param primaryStage предыдущая сцена.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Телефонная Книга");
        thisConnection = DerbyThings.makeDB("PhoneBookDB");
        contacts = DerbyThings.getFromDB(thisConnection);
        makeTable();
        BorderPane pane = new BorderPane(table);

        Scene scene = new Scene(pane, 902, 500);
        primaryStage.setScene(scene);

        makeMenus(primaryStage);

        pane.setTop(topMenuBar);
        pane.setBottom(bottomMenuBar);

        primaryStage.show();
    }

    /***
     * Метод для настройки таблицы - привязываем к ней наблюдаемую коллекцию и задаем столбцы.
     */
    private void makeTable() {
        filteredContacts = new FilteredList<Contact>(contacts.getCollection());
        updatePredicate("");
        table = new TableView<>(filteredContacts);

        table.setPlaceholder(new Label("В таблице пока нет контактов!"));

        makeColumn("Фамилия", "lastName", 100);
        makeColumn("Имя", "firstName", 100);
        makeColumn("Отчество", "patronymic", 100);
        makeColumn("Мобильный", "cellPhoneNumber", 100);
        makeColumn("Домашний", "landlinePhoneNumber", 100);
        makeColumn("Адрес", "address", 100);
        makeColumn("День рождения", "birthday", 110);
        makeColumn("Заметки", "notes", 190);
    }

    /***
     * Метод для создания столбца - задается название, свойство объекта, из которого надо брать значение,
     * желаемый размер.
     * @param title название.
     * @param property свойство, откуда надо брать информацию.
     * @param width ширина.
     */
    private void makeColumn(String title, String property, int width) {
        TableColumn<Contact, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    /***
     * Метод для создания элементов меню и нижней панели управления.
     * @param primaryStage сцена.
     */
    private void makeMenus(Stage primaryStage) {
        makeSearch();
        makeAdd(primaryStage);
        makeEdit(primaryStage);
        makeDelete();
        makeFileMenu();
        makeSettingsMenu(primaryStage);
        makeInfoMenu(primaryStage);

        bottomMenuBar = new HBox(addB, editB, deleteB, searchField, searchB);
        topMenuBar.getMenus().add(fileMenu);
        topMenuBar.getMenus().add(settingsMenu);
        topMenuBar.getMenus().add(infoMenu);
    }

    /***
     * Метод для настройки поисковой строки - при вводе энтера в строку
     * или при нажатии на кнопку обновляется предикат фильтра таблицы.
     */
    private void makeSearch() {
        searchField = new TextField();
        searchField.setPromptText("Фамилия Имя Отчество");
        searchField.setPrefWidth(352);
        searchB = new Button("Искать");
        searchB.setPrefWidth(100);

        searchField.setOnAction(event -> updatePredicate(searchField.getText()));

        searchB.setOnMouseClicked(event -> updatePredicate(searchField.getText()));
    }

    /***
     * Метод для обновления фильтра таблицы. Если введена пустая строка,
     * мы отображаем все контакты, иначе только те, ФИО которых соответствует
     * запросу по вхождению строк - не обязательно полное совпадение, мне показалось,
     * что так искать контакты логичнее.
     * @param request строка, по которой осуществляется фильтрация.
     */
    private void updatePredicate(String request) {
        if (request.isBlank()) {
            filteredContacts.setPredicate(null);
        } else {
            var parts = request.split("\\s+");
            filteredContacts.setPredicate(contact
                    -> Arrays.stream(parts).allMatch(contact.getFullName()::contains));
        }
    }

    /***
     * Метод для настройки опции меню и кнопки добавления - открывается экран добавления.
     * @param primaryStage сцена.
     */
    private void makeAdd(Stage primaryStage) {
        addB = new Button("Добавить");
        addB.setPrefWidth(150);
        addOpt = new MenuItem("Добавить");

        EventHandler<ActionEvent> add = event -> new ContactScreen(primaryStage,
                contacts, new Contact(), "Добавить");

        addOpt.setOnAction(add);
        addB.setOnAction(add);
    }

    /***
     * Метод для настройки опции меню и кнопки редактирования - проверяется, что
     * выбран какой-либо контакт из таблицы и открывается окно редактирования.
     * @param primaryStage сцена.
     */
    private void makeEdit(Stage primaryStage) {
        editB = new Button("Редактировать");
        editB.setPrefWidth(150);
        editOpt = new MenuItem("Редактировать");

        EventHandler<ActionEvent> edit = event -> {
            Contact contact = getSelected();

            if (contact != null) {
                new ContactScreen(primaryStage,
                        contacts, contact, "Изменить");
            }
        };

        editOpt.setOnAction(edit);
        editB.setOnAction(edit);
    }

    /***
     * Метод для настройки опции меню и кнопки удаления - проверяется, что выбран какой-либо
     * контакт в таблице, и далее вызывается метод удаления контакта из списка контактов.
     */
    private void makeDelete() {
        deleteB = new Button("Удалить");
        deleteB.setPrefWidth(150);
        deleteOpt = new MenuItem("Удалить");

        EventHandler<ActionEvent> delete = event -> {
            Contact contact = getSelected();
            if (contact != null) {
                contacts.deleteContact(contact);
                DerbyThings.deleteFromDB(contact, thisConnection);
            }
        };

        deleteB.setOnAction(delete);
        deleteOpt.setOnAction(delete);
    }

    /***
     * Метод для настройки меню "файл" - добавление опций,
     * настройка кнопки выхода - закрытие приложения.
     */
    private void makeFileMenu() {
        fileMenu = new Menu("Файл");
        exitOpt = new MenuItem("Выход");
        exitOpt.setOnAction(ae -> Platform.exit());

        fileMenu.getItems().addAll(addOpt, editOpt, deleteOpt,
                new SeparatorMenuItem(), exitOpt);
    }

    /***
     * Метод для настройки меню "настройки" - добавление опций.
     * @param primaryStage сцена.
     */
    private void makeSettingsMenu(Stage primaryStage) {
        settingsMenu = new Menu("Настройки");

        makeImport(primaryStage);
        makeExport(primaryStage);

        settingsMenu.getItems().addAll(importOpt, exportOpt);
    }

    /***
     * Метод для настройки опции импорта - открывается окно импорта.
     * @param primaryStage сцена.
     */
    private void makeImport(Stage primaryStage) {
        importOpt = new MenuItem("Импортировать");
        importOpt.setOnAction(event -> new FileScreen(primaryStage, contacts, "Импорт"));
    }

    /***
     * Метод для настройки опции экспорта - открывается окно экспорта.
     * @param primaryStage сцена.
     */
    private void makeExport(Stage primaryStage) {
        exportOpt = new MenuItem("Экспортировать");
        exportOpt.setOnAction(event -> new FileScreen(primaryStage, contacts, "Экспорт"));
    }

    /***
     * Метод для настройки информационного меню - открывается
     * окно информации о приложении или об авторе.
     * @param primaryStage сцена.
     */
    private void makeInfoMenu(Stage primaryStage) {
        infoMenu = new Menu("Справка");

        aboutAuthorOpt = new MenuItem("Об авторе");
        aboutAppOpt = new MenuItem("О приложении");

        aboutAuthorOpt.setOnAction(event -> new InfoScreen(primaryStage, "об авторе"));

        aboutAppOpt.setOnAction(event -> new InfoScreen(primaryStage, "о приложении"));

        infoMenu.getItems().addAll(aboutAuthorOpt, aboutAppOpt);
    }

    /***
     * Метод получения выбранного в таблице контакта с выводом предупреждения,
     * если он не выбран.
     * @return контакт, выделенный в таблице.
     */
    private Contact getSelected() {
        var target = table.getSelectionModel().getSelectedItem();
        if (target == null) {
            errorShow("Ошибка при работе с контактом!",
                    "Выбранный контакт", "вам нужно выбрать контакт в таблице");
        }
        return target;
    }

    /***
     * Метод для завершения работы приложения - перед завершением сериализуются контакты.
     */
    @Override
    public void stop() {
        try {
            DerbyThings.writeToDB(thisConnection, contacts);
            DerbyThings.closeDB(thisConnection, driver);
        } catch (Exception e) {
            errorShow("Завершение работы", "Сохранение контактов", "Что-то пошло не так");
        }
    }

    /***
     * Метод для отображения предупреждения\сообщения об ошибке.
     * @param title название.
     * @param header заголовок.
     * @param content содержание.
     */
    public static void errorShow(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        TextArea area = new TextArea(content);
        area.setWrapText(true);
        area.setEditable(false);
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }
}