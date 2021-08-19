package clientUI;

import classes.Client;
import classes.FileForTorrent;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

/***
 * Класс для основного экрана. Он состоит их таблицы файлов и нижней
 * панели с кнопками и строкой поиска.
 * В таблице отображается подробная информация о файлах, которые можно
 * загрузить с сервера, с помощью кнопок на нижнем меню можно искать файл
 * по названию, загружать файл, удалять файл из директории, куда его загрузил
 * клиент, просматривать информацию о приложении.
 */
public class MainScreen {
    Client client;
    FilteredList<FileForTorrent> filteredFiles;
    TableView<FileForTorrent> table;
    HBox bottomMenuBar;
    Stage primaryStage = new Stage();
    Button downloadB, deleteB, searchB, infoB;
    TextField searchField;

    /***
     * Константные статические значения для страшной красной рамки неправильных данных
     * и дефолтной рамки.
     */
    public static Border scaryRed = new Border(new BorderStroke(Color.RED,
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
    public static Border defaultBlack = new Border(new BorderStroke(Color.BLACK,
            BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /***
     * Конструктор с параметрами клиента, который работает с этим приложением.
     * Настраивает вызов создания элементов сцены, а также событие закрытия,
     * при котором вызывается метод клиента для закрытия сокетов и потоков.
     * @param client_ - клиент, работающий в данном интерфейсе.
     */
    public MainScreen(Client client_) {
        client = client_;
        primaryStage.setTitle("ηTorrent");

        makeTable();
        BorderPane pane = new BorderPane(table);

        Scene scene = new Scene(pane, 902, 500);
        primaryStage.setScene(scene);

        makeMenus(primaryStage);

        pane.setBottom(bottomMenuBar);

        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing Stage");
            client.closeSocket();
        });
    }

    /***
     * Метод для настройки таблицы - привязываем к ней наблюдаемую коллекцию файлов и задаем столбцы.
     * В таблице отображается название файла, директория, в которой работает сервер, размер файла в байтах
     * и директория, куда файл был загружен ("no", если пока никуда не был загружен).
     * Коллекция соответствует коллекции файлов, которые можно скачать с сервера, получаемой клиентом.
     */
    private void makeTable() {
        client.requestTable();
        ObservableList<FileForTorrent> files = client.getFiles();
        filteredFiles = new FilteredList<FileForTorrent>(files);
        updatePredicate("");
        table = new TableView<>(filteredFiles);

        table.setPlaceholder(new Label("В таблице пока нет файлов!"));

        makeColumn("Название", "name", 200);
        makeColumn("Расположение", "path", 225);
        makeColumn("Размер", "size", 200);
        makeColumn("Загружен", "downloadedTo", 225);
    }

    /***
     * Метод для создания столбца - задается название, свойство объекта, из которого надо брать значение,
     * желаемый размер.
     * @param title название.
     * @param property свойство, откуда надо брать информацию.
     * @param width ширина.
     */
    private void makeColumn(String title, String property, int width) {
        TableColumn<FileForTorrent, String> column = new TableColumn<>(title);
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
        makeDownload(primaryStage);
        makeDelete();
        makeInfo(primaryStage);
        bottomMenuBar = new HBox(downloadB, deleteB, infoB, searchField, searchB);
    }

    /***
     * Метод для настройки поисковой строки - при вводе энтера в строку
     * или при нажатии на кнопку обновляется предикат фильтра таблицы.
     */
    private void makeSearch() {
        searchField = new TextField();
        searchField.setPromptText("Название");
        searchField.setPrefWidth(352);
        searchB = new Button("Искать");
        searchB.setPrefWidth(100);

        searchField.setOnAction(event -> updatePredicate(searchField.getText()));

        searchB.setOnMouseClicked(event -> updatePredicate(searchField.getText()));
    }

    /***
     * Метод для обновления фильтра таблицы. Если введена пустая строка,
     * мы отображаем все файлы, иначе только те, название которых соответствует
     * запросу по вхождению строк - не обязательно полное совпадение, мне показалось,
     * что так искать файлы логичнее.
     * @param request строка, по которой осуществляется фильтрация.
     */
    public void updatePredicate(String request) {
        if (request.isBlank()) {
            filteredFiles.setPredicate(null);
        } else {
            var parts = request.split("\\s+");
            filteredFiles.setPredicate(file
                    -> Arrays.stream(parts).allMatch(file.getName()::contains));
        }
    }

    /***
     * Метод для настройки кнопки загрузки - проверяется, что
     * выбран какой-либо файл из таблицы, проверяется, что он весит в пределах разумного
     * (в соответствии с условием) и открывается окно загрузки этого файла, если все хорошо.
     * @param primaryStage сцена.
     */
    private void makeDownload(Stage primaryStage) {
        downloadB = new Button("Загрузить");
        downloadB.setPrefWidth(150);

        EventHandler<ActionEvent> edit = event -> {
            FileForTorrent file = getSelected();
            if (file != null) {
                if (file.getSize() <= 137438953472L) {
                    new FileScreen(this, primaryStage, client, file);
                } else {
                    errorShow("Выбор файла для загрузки", "Файл", "Вы выбрали файл, который нельзя скачать! " +
                            "Убедитесь, что вы все правильно выбрали, и размер файла не больше 128 Гб");
                }
            }
        };

        downloadB.setOnAction(edit);
    }

    /***
     * Метод для настройки кнопки получения информации - открывается окно информационной справки.
     * @param primaryStage сцена.
     */
    private void makeInfo(Stage primaryStage) {
        infoB = new Button("О приложении");
        infoB.setPrefWidth(150);

        infoB.setOnAction(event -> new InfoScreen(primaryStage));
    }

    /***
     * Метод для настройки кнопки удаления - проверяется, что выбран какой-либо
     * файл в таблице, проверяется, что он уже был загружен и далее вызывается удаление файла из
     * директории, куда он был загружен. При этом меняется его поле downloadedTo и обновляется таблица.
     * Если что-то идет не так, выводятся сообщения об ошибке.
     */
    private void makeDelete() {
        deleteB = new Button("Удалить");
        deleteB.setPrefWidth(150);

        EventHandler<ActionEvent> delete = event -> {
            FileForTorrent selected = getSelected();
            if (selected != null && !selected.getDownloadedTo().equals("no")) {
                File file = new File(selected.getDownloadedTo(), selected.getName());
                try {
                    System.out.println("deleting " + file.getAbsolutePath());
                    System.out.println(file.delete());
                    selected.setDownloadedTo("no");
                    updatePredicate(".");
                    updatePredicate("");
                    errorShow("Удаление файла", "Файл", "Файл успешно удален!");
                } catch (Exception e) {
                    e.printStackTrace();
                    errorShow("Удаление файла", "Файл", "При удалении что-то пошло не так!" +
                            " Убедитесь, что файл действительно можно удалить.");
                }

            } else {
                errorShow("Ошибка удаления!", "Удаляемый файл", "Этот файл не может быть удален!" +
                        " Убедитесь что он скачан.");
            }
        };

        deleteB.setOnAction(delete);
    }

    /***
     * Метод получения выбранного в таблице файла с выводом предупреждения,
     * если он не выбран.
     * @return файл, выделенный в таблице.
     */
    private FileForTorrent getSelected() {
        var target = table.getSelectionModel().getSelectedItem();
        if (target == null) {
            errorShow("Ошибка при работе с файлом!",
                    "Выбранный фалй", "вам нужно выбрать файл в таблице");
        }
        return target;
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