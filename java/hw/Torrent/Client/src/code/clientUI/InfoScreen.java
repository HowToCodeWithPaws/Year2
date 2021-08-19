package clientUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/***
 * Класс для экрана с информационной справкой - один для информации
 * об авторе и о приложении.
 */
public class InfoScreen {

    Stage stage = new Stage();
    GridPane pane = new GridPane();

    /***
     * Конструктор с параметрами прошлой сцены. Здесь настраивается информация
     * и ее отображение.
     * @param ownerStage - предыдущая сцена.
     */
    public InfoScreen(Stage ownerStage) {
        stage.setTitle("Информация");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(ownerStage);
        stage.setResizable(false);
        pane.setAlignment(Pos.CENTER);

        String infoText = "Данное приложение представляет собой простейший торрент.\n" +
                "При запуске клиентской части вы можете подключаться к серверу,\n" +
                "просматривать файлы в директории сервера, скачивать их в желаемую директорию.\n" +
                "Файлы в таблице можно искать по названию.\n" +
                "Также файл можно удалить из директории, в которую вы его скачали." +
                "\nНе забывайте, что допускается скачивание файлов размером до 128GiB." +
                "\nPS: пожалуйста, не ломайте приложение." + "\n\nРазработчик: БПИ195 Зубарева Наталия\n" +
                "Электронная почта: ndzubareva@edu.hse.ru\n" +
                "Профессиональные интересы: торренты на моем компьютере\n" +
                "Прочие интересы: файлы, передаваемые по TCP\n" +
                "Источник вдохновения: \"Однажды мне сказали, что μ-торрент - самый плохой из торрентов.\nТогда мне стало ясно, что я не смогу спать спокойно, пока не напишу торрент хуже.\"";

        pane.add(new Text(infoText), 0, 0);
        stage.setScene(new Scene(pane, 550, 250));
        stage.show();
    }
}
