package phoneBookUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import phoneBookModel.Contact;
import phoneBookModel.ContactList;

import java.time.LocalDate;

/***
 * Класс для редактирования\добавления контакта. Переключается
 * в зависимости от режима создания. Тут есть поля для ввода данных,
 * кнопки для отмены или сохранения и много вещей для проверки корректности.
 */
public class ContactScreen {
    Stage stage = new Stage();
    GridPane pane = new GridPane();
    TextField firstNameField, lastNameField,
            patronymicField, cellPhoneField,
            landlinePhoneField, addressField, notesField;
    DatePicker picker = new DatePicker();
    Button cancel, apply;

    /***
     * Конструктор с параметрами, из которого вызываются методы настройки всех элементов.
     * @param ownerStage предыдущая сцена.
     * @param contacts список контактов.
     * @param contact контакт, который мы добавляем (дефолтно заданный) или редактируемый.
     * @param mode режим - добавление или редактирование.
     */
    public ContactScreen(Stage ownerStage, ContactList contacts, Contact contact, String mode) {
        stageDefault(mode, ownerStage);

        makePicker(contact);

        makeTextFields(contact);

        makeButtons(mode, contacts, contact);

        addElements();

        stage.setScene(new Scene(pane, 300, 400));

        stage.show();
    }

    /***
     * Метод для первичного задания сцены.
     * @param mode режим.
     * @param ownerStage проедыдущая сцена.
     */
    private void stageDefault(String mode, Stage ownerStage) {
        stage.setTitle(mode + " контакт");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setAlignment(Pos.CENTER);
    }

    /***
     * Метод для создания поля выбора даты. Задаем значение, если оно уже есть,
     * добавляем метод для выбора даты с проверкой корректности нового значения.
     * @param contact контакт, для которого мы создаем отображение.
     */
    private void makePicker(Contact contact) {
        picker.setEditable(false);
        if (!contact.getBirthday().isEmpty()) picker.setValue(LocalDate.parse(contact.getBirthday()));

        picker.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (Contact.checkBirthday(newValue.toString())) {
                picker.setValue(newValue);
                picker.setBorder(MainScreen.defaultBlack);
            } else {
                picker.setBorder(MainScreen.scaryRed);
                MainScreen.errorShow("Ошибка в информации контакта!",
                        "День рождения", "Даты из будущего запрещены.");
            }
        }));
    }

    /***
     * Метод для создания тектовых полей - вызываем методы создания
     * полей для имен, номеров телефонов, создаем остальные здесь.
     * @param contact контакт, для которого мы создаем отображение.
     */
    private void makeTextFields(Contact contact) {
        addressField = new TextField(contact.getAddress());
        notesField = new TextField(contact.getNotes());
        firstNameField = makeNameTextField("Имя", contact.getFirstName(),
                "Имя", ", не должно быть пустым, не должно быть длиннее 32 знаков.");
        lastNameField = makeNameTextField("Фамилия", contact.getLastName(),
                "Фамилия", ", не должна быть пустой, не должна быть длиннее 32 знаков.");
        patronymicField = makeNameTextField("Отчество", contact.getPatronymic(),
                "Отчество", ".");

        makePhoneTextField(contact);
    }

    /***
     * Метод для создания текстовых полей ФИО. Задаются начальные значения
     * и подписи, добавляются листенеры, следящие за изменениями и делающие проверку
     * - если задается неправильное значение, появляется выделение поля и сообщение.
     * @param prompt подсказка для заполнения.
     * @param content содержимое - значение поля.
     * @param headerError заголовок возможного сообщения об ошибке.
     * @param contentError содержимое возможного сообщения об ошибке.
     * @return текстовое поле.
     */
    private TextField makeNameTextField(String prompt, String content, String headerError, String contentError) {
        TextField field = new TextField(content);
        field.setPromptText(prompt);
        field.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (prompt.equals("Отчество") && newValue.isEmpty() || Contact.checkName(newValue)) {
                field.setText(newValue);
                field.setBorder(MainScreen.defaultBlack);
            } else {
                field.setBorder(MainScreen.scaryRed);
                MainScreen.errorShow("Ошибка в информации контакта!", headerError,
                        "Может включать только символы латиницы или кириллицы" + contentError);
            }
        }));
        return field;
    }

    /***
     * Метод для создания полей номеров телефонов - создаются поля с подсказками
     * и начальными значениями, добавляются листенеры, проверяющие корректность изменений.
     * @param contact контакт, для которого мы создаем отображение.
     */
    private void makePhoneTextField(Contact contact) {
        cellPhoneField = new TextField(contact.getCellPhoneNumber());
        landlinePhoneField = new TextField(contact.getLandlinePhoneNumber());
        cellPhoneField.setPromptText("+79150000000");
        landlinePhoneField.setPromptText("84950000000");

        cellPhoneField.textProperty().addListener(((observable, oldValue, newValue)
                -> numbersCheck(cellPhoneField, landlinePhoneField, "Мобильный телефон", newValue)));

        landlinePhoneField.textProperty().addListener(((observable, oldValue, newValue)
                -> numbersCheck(landlinePhoneField, cellPhoneField, "Домашний телефон", newValue)));
    }

    /***
     * Метод для проверки корректности вводимого нового телефона. Так как нужно чтобы был
     * заполнен хотя бы один из двух номеров, проверка корректности также включает то,
     * чтобы оба поля не были пустыми одновременно, и чтобы редактируемое в данный момент
     * содержало корректное значение. Если что-то не так, всплывает окно предупреждения
     * и поле выделяется красной рамкой.
     * @param current редактируемое поле.
     * @param other поле другого номера телефона.
     * @param currentName название текущего номера - домашний или мобильный.
     * @param newValue значение, на которое было изменено значение в текущем поле.
     */
    private void numbersCheck(TextField current, TextField other, String currentName, String newValue) {
        if (current.getText().isBlank() && other.getText().isBlank()) {
            current.setBorder(MainScreen.scaryRed);
            other.setBorder(MainScreen.scaryRed);
            MainScreen.errorShow("Ошибка в информации контакта!", "Номера телефонов",
                    "Хотя бы один должен быть заполнен.");
        } else {
            if (Contact.checkPhoneNumber(newValue)) {
                current.setText(newValue);
                current.setBorder(MainScreen.defaultBlack);
                other.setBorder(MainScreen.defaultBlack);
            } else {
                current.setBorder(MainScreen.scaryRed);
                if (newValue.replaceAll("[^0-9]", "").length() > 11) {
                    MainScreen.errorShow("Ошибка в информации контакта!", currentName,
                            "Должен состоять из 11 цифр, разделенных пробелами или тире по " +
                                    "образцу +Х-ХХХ-ХХХ-ХХ-ХХ. Может начинаться с +7, 7 или 8.");
                }
            }
        }
    }

    /***
     * Метод для создания кнопок сохранения и отмены.
     * @param mode режим.
     * @param contacts список контактов.
     * @param contact контакт, для которого мы создаем отображение.
     */
    private void makeButtons(String mode, ContactList contacts, Contact contact) {

        makeApplyButton(mode, contacts, contact);

        cancel = new Button("Отмена");

        cancel.setOnMouseClicked(event -> stage.close());
    }

    /***
     * Метод для создания кнопки сохранения. При нажатии на нее мы проверяем корректность
     * заданных полей (выводим предупреждение, если что-то не так) и вызываем метод
     * окончания работы в зависимости от режима.
     * @param mode режим - добавление или редактирование.
     * @param contacts список контактов.
     * @param contact контакт, для которого мы создаем отображение.
     */
    private void makeApplyButton(String mode, ContactList contacts, Contact contact) {
        apply = new Button("Сохранить");
        apply.setOnMouseClicked(event -> {
            String date = picker.getValue() == null ? "" : picker.getValue().toString();
            Contact newContact = new Contact(lastNameField.getText(), firstNameField.getText(),
                    patronymicField.getText(), cellPhoneField.getText(), landlinePhoneField.getText(),
                    addressField.getText(), notesField.getText(), date);
            newContact.setId(contact.getId());
            if (Contact.checkContact(newContact)) {
                switchMode(mode, contacts, contact, newContact);
            } else {
                MainScreen.errorShow("Ошибка в информации контакта!", "Поля",
                        "Вам нужно заполнить все поля корректно для сохранения. " +
                                "Для этого обязательно заполните Имя и Фамилию (латиницей " +
                                "или кириллицей, длина не более 32 символов) и хотя бы один " +
                                "из номеров телефонов.");
            }
        });
    }

    /***
     * Метод, который сохраняет контакт - либо как новый, либо как редактированный старый.
     * @param mode режим работы.
     * @param contacts список контактов.
     * @param contact контакт, для которого мы создаем отображение.
     * @param newContact новый контакт, который мы сохраняем вместо старого значения.
     */
    private void switchMode(String mode, ContactList contacts, Contact contact, Contact newContact) {
        if (mode.equals("Добавить")) {
            if (!contacts.addContact(newContact)) {
                MainScreen.errorShow("Ошибка при добавлении контакта!", "Новый контакт",
                        "Контакт с такими ФИО уже существует! Дублирование не разрешается.");
            } else {
                stage.close();
            }
        } else {
            if (!contacts.editContact(contact, newContact)) {
                MainScreen.errorShow("Ошибка при изменении контакта!", "Изменяемый контакт",
                        "Контакт с такими ФИО уже существует! Дублирование не разрешается.");
            } else {
                stage.close();
            }
        }

    }

    /***
     * Метод для добавления всех элементов на красивую решетку окна.
     */
    private void addElements() {
        pane.add(new Text("Имя"), 0, 1);
        pane.add(new Text("Фамилия"), 0, 0);
        pane.add(new Text("Отчество"), 0, 2);
        pane.add(new Text("Мобильный"), 0, 3);
        pane.add(new Text("Домашний"), 0, 4);
        pane.add(new Text("Адрес"), 0, 5);
        pane.add(new Text("День рождения"), 0, 6);
        pane.add(new Text("Заметки"), 0, 7);
        pane.add(cancel, 0, 8);
        pane.add(firstNameField, 1, 1);
        pane.add(lastNameField, 1, 0);
        pane.add(patronymicField, 1, 2);
        pane.add(cellPhoneField, 1, 3);
        pane.add(landlinePhoneField, 1, 4);
        pane.add(addressField, 1, 5);
        pane.add(picker, 1, 6);
        pane.add(notesField, 1, 7);
        pane.add(apply, 1, 8);
    }
}
