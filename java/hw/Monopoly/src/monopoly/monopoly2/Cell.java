/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

/**
 * Абстрактный класс для клетки на доске.
 * Содержит поля координат и символа, а также
 * методы общие для всех клеток.
 */
public abstract class Cell {
    private int x, y;
    protected char symbol;

    /**
     * Метод для получения координаты х.
     *
     * @return значение координаты х клетки.
     */
    public int getX() {
        return x;
    }

    /**
     * Метод для получения координаты у.
     *
     * @return значение координаты у клетки.
     */
    public int getY() {
        return y;
    }

    /**
     * Метод для получения символа клетки в зависимости
     * от игрока, для которого клетка отображается.
     *
     * @param player игрок, смотрящий на клетку.
     * @return символ клетки.
     */
    public char getSymbol(Player player) {
        return symbol;
    }

    /**
     * Конструктор с параметрами координат и других параметров
     * (для логики клеток-детей).
     *
     * @param x_         координата х клетки.
     * @param y_         координата у клетки.
     * @param parameters параметры для клетки.
     */
    public Cell(int x_, int y_, double... parameters) {
        x = x_;
        y = y_;
    }

    /**
     * Метод для создания сообщения для игрока о нахождении
     * на клетке.
     *
     * @param player игрок, который попал на клетку.
     * @return сообщение о координатах клетки.
     */
    String message(Player player) {
        return "You are in the cell " + getX() + " " + getY() + "\n";
    }

    /**
     * Метод для определения того, предполагает ли попадание
     * на клетку диалог, следовательно нужно ли читать сообщение
     * пользователя в консоли.
     *
     * @param player игрок, попавший на клетку.
     * @return значение того, нужен ли диалог.
     */
    public abstract boolean answerNeeded(Player player);

    /**
     * Метод для обработки попадания игрока на клетку
     * с параметрами для ответа пользователя в диалоге,
     * если диалог был.
     *
     * @param player     игрок, попавший на клетку.
     * @param parameters ответ игрока в диалоге взаимодействия с клеткой.
     * @return сообщение о результате попадания на клетку.
     */
    public abstract String stepOnCell(Player player, String... parameters);
}
