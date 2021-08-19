/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

/**
 * Класс для пустой клетки, где игроки просто отдыхают.
 */
public class EmptyCell extends Cell {

    /**
     * Конструктор с координатами и заданием символа.
     *
     * @param x_ координата х.
     * @param y_ координата у.
     */
    public EmptyCell(int x_, int y_) {
        super(x_, y_);
        symbol = 'E';
    }

    /**
     * Метод сообщения игроку о попадании на клетку.
     *
     * @param player игрок, попавший на клетку.
     * @return сообщение из родительского метода + сообщение
     * о том, что в этой клетке ничего не происходит.
     */
    @Override
    String message(Player player) {
        return super.message(player) +
                "\nIt is an empty cell\nJust relax here";
    }

    /**
     * Метод, возвращающий показатель, есть ли
     * в этой клетке диалог (спойлер: нет).
     *
     * @param player игрок, попавший на клетку.
     * @return значение того, что диалога нет.
     */
    @Override
    public boolean answerNeeded(Player player) {
        return false;
    }

    /**
     * Метод попадания на клетку: единственные изменения
     * - это улучшение кармы игрока.
     *
     * @param player     игрок, попавший на клетку.
     * @param parameters ответ игрока на диалог (его не было).
     * @return сообщение об изменениях после попадания на эту клетку.
     */
    @Override
    public String stepOnCell(Player player, String... parameters) {
        return "Your karma was purified";
    }
}
