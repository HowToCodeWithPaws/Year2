/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс клетки такси, у которой есть поле дистанции,
 * на которую такси перемещает игрока, и методы для
 * реализации логики этой клетки.
 */
public class TaxiCell extends Cell {
    private int taxiDistance;

    /**
     * Конструктор с координатами и символом.
     *
     * @param x_ координата х.
     * @param y_ координата у.
     */
    public TaxiCell(int x_, int y_) {
        super(x_, y_);
        symbol = 'T';
    }

    /**
     * Метод сообщения при попадании на клетку.
     * При каждом попадании генерируется новое случайное значения
     * расстояния, на которое перемещает такси.
     *
     * @param player игрок, попавший на клетку.
     * @return сообщение о том, что сейчас произойдет.
     */
    @Override
    String message(Player player) {
        taxiDistance = ThreadLocalRandom.current().nextInt(3, 5);
        return super.message(player) + "\nIt is a taxi cell";
    }

    /**
     * Метод, возвращающий значение, есть ли здесь диалог.
     * А его нет.
     *
     * @param player игрок, попавший на клетку.
     * @return отсутствие диалога.
     */
    @Override
    public boolean answerNeeded(Player player) {
        return false;
    }

    /**
     * Метод для осуществления изменений в состоянии игрока,
     * связанных с попаданием на эту клетку - игрок двигается
     * на сгенерированное количество клеток.
     *
     * @param player     игрок, попавший на клетку.
     * @param parameters ответ игрока в диалоге. Его тут нет.
     * @return сообщение об изменении положения игрока.
     */
    @Override
    public String stepOnCell(Player player, String... parameters) {
        return player.move(taxiDistance);
    }
}
