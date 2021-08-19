/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

/**
 * Класс для клетки - офиса банка с статическими коэффициентами
 * для максимальной суммы кредита и расчета суммы долга, общими
 * для всех представителей класса. Также есть методы для реализации
 * банковских операций.
 */
public class BankCell extends Cell {
    private static double creditCoeff, debtCoeff;

    /**
     * Статический метод для задания коэффициентов для всего класса.
     *
     * @param creditCoeff_ коэффициент кредитования.
     * @param debtCoeff_   коэффициент долга.
     */
    public static void setCoeff(double creditCoeff_, double debtCoeff_) {
        creditCoeff = creditCoeff_;
        debtCoeff = debtCoeff_;
    }

    /**
     * Конструктор с координатами и заданием символа банка.
     *
     * @param x_ координата х.
     * @param y_ координата у.
     */
    public BankCell(int x_, int y_) {
        super(x_, y_);
        symbol = '$';
    }

    /**
     * Метод сообщения игроку о попадании на клетку:
     * если игроку бот, то он ничего здесь не делает,
     * если игрок должник, то он должен будет заплатить долг,
     * иначе игрок может взять кредит.
     *
     * @param player игрок, попавший в банк.
     * @return сообщение о том, что будет происходить.
     */
    @Override
    String message(Player player) {
        String output = super.message(player);
        if (player instanceof BotPlayer)
            return output +
                    "\nThis is a bank office but you are a bot\nJust relax here";
        else {
            if (!((HumanPlayer) player).owes())
                return output +
                        "\nThis is a bank office\nWould you like to get a loan?" +
                        "\nInput how many you want to get or 'No'" +
                        "\nYou can get no more than " +
                        String.format("%.2f", creditCoeff *
                                ((HumanPlayer) player).getAssets())
                        + " and not less or equal to 0";
            return output +
                    "\nThis is a bank office\nYou owe to the bank! You must pay "
                    + String.format("%.2f", ((HumanPlayer) player).getDebt());
        }
    }

    /**
     * Метод, возвращающий, что тут, вау, есть диалог, но
     * только не в том случае, если игрок - бот или должник банка.
     *
     * @param player игрок, попавший в банк.
     * @return значение того, будет ли диалог.
     */
    @Override
    public boolean answerNeeded(Player player) {
        return player instanceof HumanPlayer &&
                !((HumanPlayer) player).owes();
    }

    /**
     * Метод для завершения попадания на клетку:
     * если игрок бот, ничего не происходит, если игрок
     * - должник, с него списываются деньги, если игрок
     * не должник и отказывается брать кредит, ничего не
     * происходит, иначе вызывается метод попытки взять кредит.
     *
     * @param player     игрок, попавший на клетку.
     * @param parameters ответ игрока в диалоге.
     * @return сообщение о результате хода.
     */
    @Override
    public String stepOnCell(Player player, String... parameters) {
        if (!(player instanceof HumanPlayer)) {
            return "";
        }

        if (((HumanPlayer) player).owes()) {
            String output = player.changeBudget(-((HumanPlayer) player).getDebt());
            output += "\n" + ((HumanPlayer) player).setDebt(0);
            return output;
        } else if (parameters[0].equals("No")) {
            return "Your balance stays the same" +
                    " and you do not owe the bank anything";
        } else {
            return tryLoan(player, parameters);
        }
    }

    /**
     * Метод для попытки взять кредит - ответ игрока
     * парсится и, если парсится в сумму, которую пользователь
     * может взять, вызывается метод взятия кредита, а в случае
     * неправильного ответа ввод принудительно повторяется.
     *
     * @param player     игрок, попавший на клетку.
     * @param parameters ответ игрока в диалоге.
     * @return сообщение о результате.
     */
    public String tryLoan(Player player, String... parameters) {
        double loan;
        try {
            loan = Double.parseDouble(parameters[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Your Your input is not correct." +
                    " Answer should be either 'No' or a number. Repeat input");
        }
        if (loan > creditCoeff * ((HumanPlayer) player).getAssets() || loan <= 0) {
            throw new IllegalArgumentException("Your input is not correct." +
                    " You cannot get a loan bigger than "
                    + String.format("%.2f", creditCoeff * ((HumanPlayer) player).getAssets())
                    + " or less or equal to 0. Repeat input");
        }
        return getLoan((HumanPlayer) player, loan);
    }

    /**
     * Метод получения кредита: бюджет игрока увеличивается
     * на сумму кредита, долг устанавливается на сумму долга.
     *
     * @param player игрок, берущий кредит.
     * @param loan   сумма займа.
     * @return сообщение о результате операции.
     */
    public String getLoan(HumanPlayer player, double loan) {
        String output = player.changeBudget(loan);
        output += "\n" + player.setDebt(debtCoeff * loan);
        return output;
    }
}
