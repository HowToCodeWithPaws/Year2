/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

/**
 * Класс для клетки магазина с полями стоимости магазина,
 * коэффициентов улучшения стоимости и компенсации, компенсации, также
 * булевым флагом того, купил ли кто-то этот магазин и полем владельца.
 */
public class ShopCell extends Cell {
    private double priceN, improvementCoeff, compensationCoeff, compensationK;
    private boolean taken = false;
    private Player owner;

    /**
     * Конструктор, который принимает координаты магазина,
     * а также все коэффициенты, описанные выше.
     * Зудается дефолтный символ некупленного магазина
     * и проверяется число входных параметров.
     *
     * @param x_         координата х.
     * @param y_         координата у.
     * @param parameters коэффициенты цены, величин улучшений, компенсации.
     */
    public ShopCell(int x_, int y_, double... parameters) {
        super(x_, y_);
        symbol = 'S';
        if (parameters.length < 4) {
            throw new IllegalArgumentException("More arguments are required!");
        }

        priceN = parameters[0];
        compensationK = parameters[1];
        improvementCoeff = parameters[2];
        compensationCoeff = parameters[3];
    }

    /**
     * Метод для получения символа, который видит игрок:
     * если магазин ничей, он обозначается дефолтно,
     * иначе для владельца он обозначается как М, а для
     * противника как О.
     *
     * @param player игрок, попавший на клетку.
     * @return символ с отображением.
     */
    public char getSymbol(Player player) {
        if (!taken) return 'S';
        else {
            if (player == owner) return 'M';
            return 'O';
        }
    }

    /**
     * Метод о сообщении о попадании на клетку.
     * Если магазини свободен, выводится предложение его купить
     * за его стоимость, если игрок - владелец, предлагается
     * сделать улучшение магазина, иначе выводится сообщение о том,
     * что игрок платит компенсацию владельцу магазина.
     *
     * @param player игрок, попавший в магазин.
     * @return строка с сообщением игроку.
     */
    String message(Player player) {
        String output = super.message(player);
        if (!taken)
            return output + "\nThis is a shop cell" +
                    "\nThis shop has no owner. Would you like to buy it for "
                    + String.format("%.2f", priceN) +
                    "$?\nInput 'Yes' if you agree or 'No' otherwise";
        else {
            if (player == owner)
                return output + "\nThis is your shop\nWould you like to upgrade it for "
                        + String.format("%.2f", improvementCoeff * priceN) +
                        "$?\nInput 'Yes' if you agree or 'No' otherwise";
            return output + "\nThis is your opponent's shop" +
                    "\nYou have to pay a compensation of "
                    + String.format("%.2f", compensationK);
        }
    }

    /**
     * Метод, показывающий, что вау, здесь наконец-то мы ждем от
     * игрока ответ, но только если магазин свободен или игрок - его
     * владелец.
     *
     * @param player игрок, зашедший в магазин.
     * @return значение того, будем ли мы спрашивать у игрока.
     */
    @Override
    public boolean answerNeeded(Player player) {
        return !taken || player == owner;
    }

    /**
     * Метод завершения попадания на клетку. Если магазин
     * свободен и игрок решил его купить, вызываем метод покупки
     * магазина, иначе ничего не меняется. Если игрок владелец
     * и решил улучшить магазин, вызываем метод улучшения, иначе ничего
     * не меняем. Если игрок попал на магазин соперника, списываем с
     * его счета компенсацию. Везде происходит проверка корректности
     * вводимого ответа.
     *
     * @param player игрок, попавший на клетку.
     * @param answer ответ игрока в диалоге.
     * @return строка с изменением по результату попадания на клетку.
     */
    public String stepOnCell(Player player, String... answer) {
        if (!taken) {
            if (answer[0].equals("Yes")) return buyShop(player);
            if (answer[0].equals("No")) return "Your balance does not change" +
                    " and this shop remains without an owner";
            throw new IllegalArgumentException("Your input is not correct. " +
                    "Answer should be either 'Yes' or 'No'. Repeat input");
        } else {
            if (player == owner) {
                if (answer[0].equals("Yes")) return improveShop(player);
                if (answer[0].equals("No")) return "Your balance does not change" +
                        " and this shop remains without improvement";
                throw new IllegalArgumentException("Your input is not correct. " +
                        "Answer should be either 'Yes' or 'No'. Repeat input");
            }
            return player.changeBudget(-compensationK) +
                    "\nMessage to " + owner.getName() + ": " +
                    owner.changeBudget(compensationK);
        }
    }

    /**
     * Метод для получения стоимости магазина.
     *
     * @return стоимость магазина.
     */
    public double getPrice() {
        return priceN;
    }

    /**
     * Метод для покупки магазина: у игрока
     * списываются деньги, если он этим себя обанкротил,
     * выводится сообщение об этом, далее в поле владельца
     * магазина записывается игрок, флаг свободности магазина
     * меняется, вызывается метод прибавления магазина к
     * списку магазинов игрока.
     *
     * @param player игрок, покупающий магазин.
     * @return строка о результатах покупки.
     */
    public String buyShop(Player player) {
        String output = player.changeBudget(-priceN);
        if (player.isBankrupt()) {
            return output;
        }
        owner = player;
        taken = true;
        output += "\n" + player.addShop(this);
        return output;
    }

    /**
     * Метод для улучшения магазина: со счета игрока
     * списывается стоимость улучшения, если игрок себя
     * этим обанкротил, выводится информация об этом.
     * Иначе цена увеличивается на себя*коффициент улучшения,
     * компенсация аналогично, но с коэффициентом компенсации.
     *
     * @param player игрок, улучшающий магазин.
     * @return строка о результате улучшения.
     */
    public String improveShop(Player player) {
        String output = player.changeBudget(-improvementCoeff * priceN);
        if (player.isBankrupt()) {
            return output;
        }
        priceN += improvementCoeff * priceN;
        compensationK += compensationCoeff * compensationK;
        return "Shop was improved! Its price is now " + String.format("%.2f", priceN) +
                " and its compensation is " + String.format("%.2f", compensationK) +
                "\n" + output;
    }
}
