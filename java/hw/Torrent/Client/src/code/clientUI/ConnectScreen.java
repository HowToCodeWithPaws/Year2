package clientUI;

import classes.Client;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/***
 * Это класс для экрана, с помощью которого пользователь-клиент подключается к
 * серверу. Здесь есть поля для ввода хоста и порта, кнопка подключения.
 */
public class ConnectScreen extends Application {
    GridPane pane = new GridPane();
    TextField hostField, portField;
    Button cancel, connect;
    Client client;

    /***
     * Метод для начала работы экрана. Задаются все параметры отображения.
     * @param stage - сцена экрана.
     */
    @Override
    public void start(Stage stage) {
        client = new Client();
        System.out.println("new client");
        stageDefault(stage);
        makeTextFields();
        makeButtons(stage);
        addElements();
        stage.setScene(new Scene(pane, 300, 200));
        stage.show();
    }

    /***
     * Метод для запуска.
     * @param args параметры запуска приложения.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /***
     * Метод для задания дефолтной сцены с названием, объектами.
     * @param stage - сцена экрана.
     */
    private void stageDefault(Stage stage) {
        stage.setTitle("Подключение");
        stage.setResizable(false);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setAlignment(Pos.CENTER);
    }

    /***
     * Метод для задания текстовых полей для ввода значения порта и хоста.
     * Задаются методы реакции на изменение этих полей с проверкой корректности.
     */
    private void makeTextFields() {
        hostField = new TextField();
        portField = new TextField();
        hostField.setPromptText("localhost");

        hostField.textProperty().addListener(((observable, oldValue, newValue)
                -> {
            if (!newValue.isBlank()) {
                hostField.setText(newValue);
                hostField.setBorder(MainScreen.defaultBlack);
            } else {
                hostField.setBorder(MainScreen.scaryRed);
                MainScreen.errorShow("Ошибка в заполнении адреса!", "Хост",
                        "Не должен быть пустым");
            }
        }));

        portField.setPromptText("2345");

        portField.textProperty().addListener(((observable, oldValue, newValue)
                -> {
            if (!newValue.isBlank()) {
                try {
                    Integer.parseInt(newValue);
                    portField.setText(newValue);
                    portField.setBorder(MainScreen.defaultBlack);
                } catch (Exception e) {

                    portField.setBorder(MainScreen.scaryRed);
                    MainScreen.errorShow("Ошибка в заполнении адреса!", "Порт",
                            "Должен быть числом");
                }
            } else {
                portField.setBorder(MainScreen.scaryRed);
                MainScreen.errorShow("Ошибка в заполнении адреса!", "Порт",
                        "Не должен быть пустым, может содержать только цифры");
            }
        }));

    }

    /***
     * Метод для создания кнопок, в частности вызов метода создания кнопки
     * подключения и создание кнопки выхода, которая закрывает сцену.
     * @param stage - сцена экрана.
     */
    private void makeButtons(Stage stage) {
        makeApplyButton(stage);
        cancel = new Button("Отмена");
        cancel.setOnMouseClicked(event -> stage.close());
    }

    /***
     * Метод для настройки кнопки подключения, при нажатии происходит вызов
     * метода клиента для подключения к серверу.
     * @param stage - сцена экрана.
     */
    private void makeApplyButton(Stage stage) {
        connect = new Button("Подключиться");
        connect.setOnMouseClicked(event -> {

            try {
                Integer port = Integer.parseInt(portField.getText());
                if (client.tryConnect(hostField.getText(), port)) {
                    System.out.println("connected");
                    client.greetings();
                    System.out.println("done greetings");
                    new MainScreen(client);
                    stage.close();
                } else {
                    hostField.setBorder(MainScreen.scaryRed);
                    portField.setBorder(MainScreen.scaryRed);
                    MainScreen.errorShow("Ошибка при подключении!", "Хост и порт",
                            "Вам нужно заполнить поля доступа к хосту и порту в соответствии с адресом, по которому расположен сервер.");
                }
            } catch (NumberFormatException e) {
                portField.setBorder(MainScreen.scaryRed);
                MainScreen.errorShow("Ошибка в заполнении адреса!", "Порт",
                        "Должен быть числом");
            }
        });
    }

    /***
     * Метод для добавления всех элементов на красивую решетку окна.
     */
    private void addElements() {
        pane.add(new Text("Хост"), 0, 0);
        pane.add(new Text("Порт"), 0, 1);
        pane.add(hostField, 1, 0);
        pane.add(portField, 1, 1);

        pane.add(cancel, 0, 8);
        pane.add(connect, 1, 8);
    }
}
