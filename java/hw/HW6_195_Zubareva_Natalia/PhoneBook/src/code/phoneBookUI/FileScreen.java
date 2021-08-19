package phoneBookUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import phoneBookModel.ContactList;

import java.io.File;
import java.util.ArrayList;

/***
 * Класс для экрана экспорта\импорта. Имеет поле для выбора адреса файла, текста,
 * сообщающего об успешности операции и кнопки отмены или сохранения.
 */
public class FileScreen {
    Stage stage = new Stage();
    GridPane pane = new GridPane();
    GridPane paneTop = new GridPane();
    GridPane paneBottom = new GridPane();
    FileChooser chooser = new FileChooser();
    Button cancel, apply;
    TextField pathField = new TextField();
    Text message = new Text();
    File file;

    /***
     * Конструктор с параметрами. Вызывает методы создания всех частей окна.
     * @param ownerStage предыдущая сцена.
     * @param contacts список контактов.
     * @param mode режим работы - импорт или экспорт.
     */
    public FileScreen(Stage ownerStage, ContactList contacts, String mode) {
        stageDefault(mode, ownerStage);
        makeChooser(mode);
        makeChoiceHandling(mode);
        makeApplyButton(mode, contacts);
        makeCancelButton();
        makeTopPane();
        makeBottomPane();
        addElements();

        stage.setScene(new Scene(pane, 302, 300));
        stage.show();
    }

    /***
     * Метод для дефолтного задания сцены, названия размеров и тд.
     * @param mode режим работы.
     * @param ownerStage предыдущая сцена.
     */
    private void stageDefault(String mode, Stage ownerStage) {
        stage.setTitle(mode + " контактов");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setAlignment(Pos.CENTER);
    }

    /***
     * Метод для настройки нативного выбора файла с определением формата.
     * @param mode режим.
     */
    private void makeChooser(String mode) {
        chooser.setTitle("Выберите файл для " + mode.toLowerCase() + "a");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("DAT files (*.dat)", "*.dat");
        chooser.getExtensionFilters().add(extFilter);
    }

    /***
     * Метод для настройки того, что происходит при выборе файла. При клике на поле адреса
     * открывается окно выбора, далее если адрес не выбран, выводится предупреждение, иначе
     * обновляется текст адреса и поля отчета об успешности операции - можно осуществить
     * желаемое взаимодействие с информацией из файла, нажав сохранить.
     * @param mode режим работы.
     */
    private void makeChoiceHandling(String mode) {
        pathField.setEditable(false);
        pathField.setPrefWidth(200);
        pathField.setOnMouseClicked(event -> {
            file = chooser.showOpenDialog(stage);
            if (file == null) {
                pathField.setText("");
                MainScreen.errorShow(mode + " контактов",
                        "Путь к файлу", "Вам нужно выбрать файл!");
                message.setText("Вам нужно выбрать файл!");
                pathField.setBorder(MainScreen.scaryRed);
            } else {
                pathField.setBorder(MainScreen.defaultBlack);
                pathField.setText(file.getPath());
                message.setText("Файл для " + mode.toLowerCase() +
                        "а контактов выбран.\nНажмите \"Сохранить\",\nесли хотите " +
                        mode.toLowerCase() + "ировать их.");
            }
        });
    }

    /***
     * Метод для настройки кнопки сохранения - в случае режима экспорта при нажатии
     * на нее контакты записываются в файл, если же режим импорта, то вызывается его метод.
     * @param mode режим.
     * @param contacts список контактов.
     */
    private void makeApplyButton(String mode, ContactList contacts) {
        apply = new Button("Сохранить");
        apply.setOnMouseClicked(event -> {
            switch (mode) {
                case "Экспорт":
                    try {
                        ContactList.writeToFile(file.getPath(), contacts);
                        message.setText("Контакты успешно экспортированы!");
                    } catch (Exception e) {
                        message.setText("Контакты не были экспортированы, так как произошли ошибки." +
                                "\nПопробуйте снова.");
                        MainScreen.errorShow("Экспорт контактов", "Запись в файл",
                                "Что-то пошло не так! Попробуйте заново.");
                    }
                    break;
                case "Импорт":
                    importContacts(contacts);
                    break;
            }
        });
    }

    /***
     * Метод импортирования. Контакты считываются из файла, производится попытка их
     * добавления с выводом в текстовое поле всех возникающих конфликтов.
     * @param contacts список контактов.
     */
    private void importContacts(ContactList contacts) {
        ContactList importContacts;
        try {
            importContacts = ContactList.readFromFile(file.getPath());
            ArrayList<String> errors = contacts.addContacts(importContacts);
            if (errors.isEmpty()) {
                message.setText("Контакты успешно импортированы!");
            } else {
                message.setText("Все корректные контакты добавлены.\nПри попытке добавления остальных " +
                        "возникли следующие ошибки:\n" + String.join("\n\n", errors));
            }
        } catch (Exception e) {
            message.setText("Контакты не были добавлены, так как данные в файле был некорректны." +
                    "\nПопробуйте с другим файлом.");
            MainScreen.errorShow("Импорт контактов", "Чтение из файла и добавление",
                    "Данные в файле были некорректны! Попробуйте с другим файлом.");
        }
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
     */
    private void makeTopPane() {
        paneTop.setHgap(10);
        paneTop.setVgap(10);
        paneTop.setPadding(new Insets(5, 5, 5, 5));
        paneTop.setAlignment(Pos.TOP_LEFT);
        paneTop.add(new Text("Путь к файлу"), 0, 0);
        paneTop.add(pathField, 1, 0);
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
        ScrollPane scroll = new ScrollPane(message);
        scroll.setPrefWidth(300);
        scroll.setPrefHeight(200);
        message.wrappingWidthProperty().bind(scroll.prefWidthProperty());

        pane.add(paneTop, 0, 0);
        pane.add(scroll, 0, 1);
        pane.add(paneBottom, 0, 2);
    }
}
