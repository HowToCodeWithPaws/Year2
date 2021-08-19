/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

/**
 * Класс для штрафной клетки, помимо всего прочего,
 * у него есть статическое поле штрафа, общее для
 * всех представителей этого класса клеток.
 */
public class PenaltyCell extends Cell {
    private static double penaltyCoeff;

    /**
     * Статический метод для задания коэффициента штрафа
     * для всех клеток этого класса.
     *
     * @param penaltyCoeff_ значения штрафного коэффициента.
     */
    public static void setCoeff(double penaltyCoeff_) {
        penaltyCoeff = penaltyCoeff_;
    }

    /**
     * Конструктор с координатами и символом.
     *
     * @param x_ координата х.
     * @param y_ координата у.
     */
    public PenaltyCell(int x_, int y_) {
        super(x_, y_);
        symbol = '%';
    }

    /**
     * Метод сообщения о попадании на клетку.
     * Это штрафная клетка, при попадании на нее у игрока
     * списываются деньги в указанном количестве.
     *
     * @param player игрок, попавший сюда.
     * @return сообщение о списании денег.
     */
    @Override
    String message(Player player) {
        return super.message(player) +
                "\nThis is a penalty cell! Oh no!\nYou have to pay "
                + String.format("%.2f", penaltyCoeff);
    }

    /**
     * Метод для получения того, есть ли в этой клетки диалог.
     * Его нет. Мнение игрока о происходящем никакой роли не играет.
     *
     * @param player несчастный, попавший на эту клетку.
     * @return значение того, что диалога здесь нет.
     */
    @Override
    public boolean answerNeeded(Player player) {
        return false;
    }

    /**
     * Метод для завершения попадания на клетку: у игрока
     * из бюджета вычитается сумма, оговоренная в условии.
     *
     * @param player     игрок, попавший сюда.
     * @param parameters ответ игрока в дилоге (его не было).
     * @return сообщение о списании денег у игрока.
     */
    @Override
    public String stepOnCell(Player player, String... parameters) {
        return player.changeBudget(-penaltyCoeff * player.getBudget());
    }
}
