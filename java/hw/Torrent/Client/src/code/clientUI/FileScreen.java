package clientUI;

import classes.Client;
import classes.FileForTorrent;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

/***
 * Класс для экрана загрузки файла. Имеет поле для выбора адреса папки, в которую
 * будет осуществляться загрузка, текста, сообщающего об успешности операции,
 * прогресс бара для отображения статуса выполнения загрузки и кнопки отмены или сохранения.
 */
public class FileScreen {
    MainScreen parent;
    Stage stage = new Stage();
    GridPane pane = new GridPane();
    GridPane paneTop = new GridPane();
    GridPane paneBottom = new GridPane();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    Button cancel, apply;
    ProgressBar progressBar = new ProgressBar(0);
    Text progress = new Text("|............|............|............|............" +
            "|............|............|............|............|............|............|\n" +
            "0% 10%   20%    30%   40%    50%    60%   70%   80%   90% 100%");
    TextField pathField = new TextField();
    Text message = new Text();
    File file;
    Task thisDownload;
    boolean inProgress = false;

    /***
     * Конструктор с параметрами. Настраивает отображение, вызывает методы создания
     * всех элементов экрана.
     * @param parent_ - родительский экран с таблицей файлов.
     * @param ownerStage - предыдущая сцена.
     * @param client - клиент, работающий в этом приложении.
     * @param file - файл, который мы собираемся загружать.
     */
    public FileScreen(MainScreen parent_, Stage ownerStage, Client client, FileForTorrent file) {
        parent = parent_;
        stageDefault(ownerStage);
        makeChoiceHandling();
        makeApplyButton(client, file);
        makeCancelButton();
        makeTopPane(file);
        makeBottomPane();
        addElements();
        stage.setScene(new Scene(pane, 352, 400));
        stage.show();
    }

    /***
     * Метод для настройки внешнего вида сцены. Задает реакцию на попытку закрытия,
     * чтобы окно нельзя было закрыть во время загрузки, вызывает методы настройки.
     * @param ownerStage - предыдущая сцена.
     */
    private void stageDefault(Stage ownerStage) {
        stage.setTitle("Загрузка файла");
        inProgress = false;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (inProgress) {
                    event.consume();
                }
            }
        });
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setAlignment(Pos.CENTER);
        pathField.setMaxWidth(150);
        pathField.setPrefWidth(150);
    }

    /***
     * Метод для настройки выбора директории для сохранения - при нажатии
     * на текстовое поле с путем к директории открывается диалоговое окно
     * с проверкой корректности ввода.
     */
    private void makeChoiceHandling() {
        directoryChooser.setTitle("Выберите директорию для сохранения");
        pathField.setEditable(false);
        pathField.setPrefWidth(200);
        pathField.setOnMouseClicked(event -> {
            file = directoryChooser.showDialog(stage);
            if (file == null) {
                pathField.setText("");
                MainScreen.errorShow("Загрузка файла",
                        "Путь к директории", "Вам нужно выбрать директорию!");
                message.setText("Вам нужно выбрать директорию!");
                apply.setDisable(true);
                pathField.setBorder(MainScreen.scaryRed);
            } else {
                pathField.setBorder(MainScreen.defaultBlack);
                pathField.setText(file.getAbsolutePath());
                message.setText("Директория для загрузки файла выбрана." +
                        "\nНажмите \"Загрузить\",\nесли хотите " +
                        "загрузить туда файл.");
                apply.setDisable(false);
            }
        });
    }

    /***
     * Метод для создания таска для загрузки. Нужен для того, чтобы
     * интерфейс не зависал на время выполнения и правильно отображал
     * прогресс. Настриваются в основном получение таска от клиента и
     * прогресс бар.
     * @param client - клиент, который загружает файл.
     * @param file - загружаемый файл.
     * @param dir - выбранная директория для загрузки файла.
     */
    private void makeTask(Client client, FileForTorrent file, String dir) {
        progressBar.setProgress(0);
        progressBar.progressProperty().unbind();
        ObservableValue property = client.getTaskDownload(file, dir).progressProperty();
        progressBar.progressProperty().bind(property);
        thisDownload = new Task() {
            @Override
            protected Object call() {
                try {
                    client.downloadStart();
                    file.setDownloadedTo(dir);
                    inProgress = false;
                    cancel.setDisable(false);
                    parent.updatePredicate(".");
                    parent.updatePredicate("");
                    message.setText("Файл успешно загружен! Можете покинуть этот экран.");
                } catch (Exception e) {
                    e.printStackTrace();
                    MainScreen.errorShow("Загрузка файла",
                            "Путь к директории", "Вам нужно выбрать директорию!");
                }
                return null;
            }
        };
    }


    /***
     * Метод для настройки кнопки подтверждения загрузки - при нажатии на
     * кнопку создается и запускается таск, соответствующий таску загрузки в
     * классе клиента. При этом все кнопки становятся неактивными, чтобы
     * пользователь не наделал каких-то гадостей, пока идет загрузка.
     * @param client - клиент, загружающий файл.
     * @param file - загружаемый файл.
     */
    private void makeApplyButton(Client client, FileForTorrent file) {
        apply = new Button("Загрузить");
        inProgress = true;
        apply.setDisable(true);
        apply.setOnMouseClicked(event -> {
            apply.setDisable(true);
            cancel.setDisable(true);
            pathField.setDisable(true);
            message.setText("Начинаем загружать файл. Пожалуйста, подождите.");
            makeTask(client, file, pathField.getText());
            Thread t = new Thread(thisDownload);
            t.setDaemon(true);
            t.start();
        });
    }


    /***
     * Метод для кнопки отмены - при нажатии на нее окно закрывается.
     */
    private void makeCancelButton() {
        cancel = new Button("Отмена");
        cancel.setOnMouseClicked(event -> stage.close());
    }

    /***
     * Метод для настройки верхней панели.
     * @param file - загружаемый файл.
     */
    private void makeTopPane(FileForTorrent file) {
        paneTop.setHgap(10);
        paneTop.setVgap(10);
        paneTop.setPadding(new Insets(5, 5, 5, 5));
        paneTop.setAlignment(Pos.TOP_LEFT);
        Text text = new Text("Файл " + file.getName());
        text.setWrappingWidth(150);
        paneTop.add(text, 0, 0);
        paneTop.add(new Text("размером " + file.getSize().toString() + " байт"), 1, 0);
        paneTop.add(new Text("Загрузить в"), 0, 1);
        paneTop.add(pathField, 1, 1);
    }

    /***
     * Метод для настройки нижней панели.
     */
    private void makeBottomPane() {
        paneBottom.setHgap(10);
        paneBottom.setVgap(10);
        paneBottom.setPadding(new Insets(5, 5, 5, 5));
        paneBottom.setAlignment(Pos.BOTTOM_RIGHT);
        paneBottom.add(cancel, 0, 0);
        paneBottom.add(apply, 1, 0);
    }

    /***
     * Метод для добавления элементов. Все по красоте.
     */
    private void addElements() {
        progressBar.setPrefSize(350, 30);
        ScrollPane scroll = new ScrollPane(message);
        scroll.setPrefWidth(300);
        scroll.setPrefHeight(200);
        message.wrappingWidthProperty().bind(scroll.prefWidthProperty());

        pane.add(paneTop, 0, 0);
        pane.add(scroll, 0, 1);
        pane.add(progressBar, 0, 2);
        pane.add(progress, 0, 3);
        pane.add(paneBottom, 0, 4);
    }
}
