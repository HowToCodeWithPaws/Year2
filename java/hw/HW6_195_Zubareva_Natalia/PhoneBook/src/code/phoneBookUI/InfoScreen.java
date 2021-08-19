package phoneBookUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/***
 * Класс для экрана с информационной справкой - один для информации
 * об авторе и о приложении, информация переключается в зависимости от режима.
 */
public class InfoScreen {

    Stage  stage = new Stage();
    GridPane pane= new GridPane();

    /***
     * Конструктор с параметрами.
     * @param ownerStage предыдущая сцена.
     * @param mode режим - об авторе, либо о приложении.
     */
    public InfoScreen(Stage ownerStage, String mode) {
        stage.setTitle("Информация " + mode);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        pane.setAlignment(Pos.CENTER);

        String infoText = mode.equals("об авторе") ? "Разработчик: БПИ195 Зубарева Наталия\n" +
                "Электронная почта: ndzubareva@edu.hse.ru\n" +
                "Профессиональные интересы: телефонные книги на javafx\n" +
                "Прочие интересы: картинки с котиками"
                : "Данное приложение представляет собой телефонную книгу.\n" +
                "Вы можете добавлять, изменять, удалять контакты," +
                "\nзагружать и выгружать их из файлов формата .dat,\n" +
                "искать по ФИО и ... смотреть на них." +
                "\nНе забывайте, что контакт определяется уникальностью ФИО,\n" +
                "не пытайтесь добавить двух идентичных тезок." +
                "\nPS: пожалуйста, не ломайте приложение.";

        pane.add(new Text(infoText), 0, 0);
        stage.setScene(new Scene(pane, 350, 125));
        stage.show();
    }
}
