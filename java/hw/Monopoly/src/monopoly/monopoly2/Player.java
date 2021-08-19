/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

import java.util.ArrayList;

/**
 * Абстрактный класс для игрока с дефолтным функционалом:
 * у игрока есть бюджет, имя, список купленных магазинов
 * и позиция, а также методы, общие для бота и
 * игрока-человека.
 */
public abstract class Player {
    private double budget = 0;
    private String name;
    protected ArrayList<ShopCell> ownedShops;
    private int position = 0;

    /**
     * Конструктор с параметрами для создания игрока:
     * задается имя и бюджет, формируется пустой список
     * купленных магазинов.
     *
     * @param name_   имя игрока.
     * @param budget_ бюджет игрока.
     */
    public Player(String name_, int budget_) {
        name = name_;
        budget += budget_;
        ownedShops = new ArrayList<ShopCell>();
    }

    /**
     * Метод для получения имени игрока.
     *
     * @return Возвращает имя игрока.
     */
    public String getName() {
        return name;
    }

    /**
     * Метод для получения бюджета игрока.
     * Если бюджет отрицательный, выводится 0ж
     *
     * @return 0 или бюджет, если бюждет больше 0.
     */
    public double getBudget() {
        return budget > 0 ? budget : 0;
    }

    /**
     * Метод для изменения бюджета на определенную сумму.
     * Нужен для операций покупок, списания penalty и
     * всего остального, что происходит с бюджетом.
     *
     * @param amount значение, на которое мы изменяем бюджет.
     * @return сообщение об операции изменения.
     */
    public String changeBudget(double amount) {
        budget += amount;
        return "Your budget is changed by " + String.format("%.2f", amount);
    }

    /**
     * Метод перемещения игрока на некоторое число шагов.
     * К позиции на доске прибавляется число, затем позиция
     * нормируется по размеру доски.
     *
     * @param steps число шагов на которое мы перемещаемся.
     * @return сообщение о перемещении.
     */
    public String move(int steps) {
        position += steps;
        position %= Board.size();
        return "You move " + steps + " cells forward";
    }

    /**
     * Метод добавления магазина в список купленных магазинов
     * с сообщением о добавления.
     *
     * @param shop передаваемый магазин для добавления.
     * @return сообщение о владении магазином.
     */
    public String addShop(ShopCell shop) {
        ownedShops.add(shop);
        return "You now own this shop";
    }

    /**
     * Метод для получения информации о том, обанкротился ли
     * игрок. Сравнивает бюджет с 0 и возвращает boolean.
     * Нужен для понимания, пора ли заканчивать игру.
     *
     * @return значение того, обанкротился ли игрок.
     */
    public boolean isBankrupt() {
        return budget <= 0;
    }

    /**
     * Метод для возвращения краткого описания состояния игрока:
     * его позиции на доске и бюджета. Нужен для вывода в консоль
     * актуального состояния.
     *
     * @return строка со статусом игрока.
     */
    public String status() {
        return getName() + "'s position is " + stringPosition() +
                "\n" + getName() + "'s budget is " + String.format("%.2f", getBudget());
    }

    /**
     * Метод для получения позиции игрока на доске.
     *
     * @return позиция в формате номера клетки.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Метод для вывода позиции игрока в координатах х и у.
     *
     * @return строка с координатами игрока.
     */
    public String stringPosition() {
        int x = Board.makeNToXY(position)[0];
        int y = Board.makeNToXY(position)[1];
        return x + " " + y;
    }
}
