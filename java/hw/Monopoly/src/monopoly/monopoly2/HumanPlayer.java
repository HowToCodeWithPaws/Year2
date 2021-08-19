/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

/**
 * Класс - наследник Player с реализацией функций для
 * банковых операций. У игрока-человека есть поле долга
 * и методы.
 */
public class HumanPlayer extends Player {
    private double debt = 0;

    /**
     * Конструктор с параметрами, вызывает родительский конструктор.
     *
     * @param name_   имя игрока.
     * @param budget_ бюджет игрока.
     */
    public HumanPlayer(String name_, int budget_) {
        super(name_, budget_);
    }

    /**
     * Метод для получения долга игрока.
     *
     * @return число-долг.
     */
    public double getDebt() {
        return debt;
    }

    /**
     * Метод для возвращения статуса игрока относительно банка:
     * если долг больше 0, то игрок- должник.
     *
     * @return значение того, должник ли игрок.
     */
    public boolean owes() {
        return debt > 0;
    }

    /**
     * Метод установки долга. Используется при взятии
     * и при возвращении кредита.
     *
     * @param debt_ сумма долга.
     * @return сообщение об установке нового размера долга.
     */
    public String setDebt(double debt_) {
        debt = debt_;
        if (isBankrupt()) {
            return "Your debt cannot be returned";
        }
        return "Your debt is now " + String.format("%.2f", debt);
    }

    /**
     * Метод для получения стоимости всех магазинов игрока с
     * их улучшениями. Используется для расчета максимальной
     * суммы кредита.
     *
     * @return стоимость вложений игрока.
     */
    public double getAssets() {
        double assets = 0;
        for (ShopCell shop : ownedShops) {
            assets += shop.getPrice();
        }
        return assets;
    }
}
