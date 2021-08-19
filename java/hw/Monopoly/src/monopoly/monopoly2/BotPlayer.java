/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс - наследник Player для игрока-бота.
 * Имеет метод для принятия случайного решения
 * и не имеет функионала для взаимодействия с банком.
 */
public class BotPlayer extends Player {
    /**
     * Конструктор, вызывающий родительский конструктор.
     *
     * @param name_   имя.
     * @param budget_ бюджет.
     */
    public BotPlayer(String name_, int budget_) {
        super(name_, budget_);
    }

    /**
     * Метод для случайного принятия решения.
     *
     * @return строка - ответ, да или нет.
     */
    public String makeDecision() {
        if (ThreadLocalRandom.current().nextBoolean()) return "Yes";
        return "No";
    }
}
