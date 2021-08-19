/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для непосредственно процесса игры.
 * Здесь есть поля входных параметров: высоты,
 * ширины поля, стартового бюджета игроков,
 * булевые флаги на повторный ввод и начало игры,
 * экземпляр игрового поля, два экземпляра игроков,
 * сканнер для ввода.
 */
public class Monopoly {
    static int height, width, money;
    static boolean redoInput = false, play = true;
    static Board board;
    static Player firstPlayer, secondPlayer;
    static Scanner in = new Scanner(System.in);

    /**
     * Входная точка приложения, проверяется количество входных
     * параметрах, при неверном количестве игра не начинается,
     * параметры парсятся, затем зпускается инициализация игры.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {

        if (args == null || args.length < 3) {
            System.out.println("Not enough input parameters for the game!");
            play = false;
        } else {
            try {
                inputArgs(args);
            } catch (NumberFormatException e) {
                System.out.println("Input parameters are not integers!");
                play = false;
            }
        }

        if (play) initialize();
        else System.out.println("Run the game again with correct input.");
    }

    /**
     * Метод инициализации игры: формируется поле, случайным образом
     * выбирается, кто второй и первый игрок - бот или человек. Выводится
     * информация об игре, если все пераметры верны, вызывается метод игры.
     */
    static void initialize() {

        board = new Board(height, width);

        if (ThreadLocalRandom.current().nextBoolean()) {
            firstPlayer = new BotPlayer("Bot", money);
            secondPlayer = new HumanPlayer("Human", money);
            System.out.println("The first player is Bot and the second is Human");
        } else {
            firstPlayer = new HumanPlayer("Human", money);
            secondPlayer = new BotPlayer("Bot", money);
            System.out.println("The first player is Human and the second is Bot");
        }
        System.out.println("Starting budget is " + money);

        System.out.println(board.field(null));

        in.nextLine();

        System.out.println(play());
    }

    /**
     * Метод игры: для первого и второго игрока по очереди
     * выводятся их статусы, потом вызывается метод хода,
     * выводится статус после изменения, проверяется, не
     * проиграли ли они. Если никто не проиграл, игра
     * продолжается.
     *
     * @return строка с ходом игры.
     */
    static String play() {
        System.out.println(firstPlayer.status() + "\n");
        makeAMove(firstPlayer);
        System.out.println("\n" + firstPlayer.status());
        System.out.println(board.field(firstPlayer));
        in.nextLine();
        if (firstPlayer.isBankrupt()) {
            return "\nPLAYER " + firstPlayer.getName().toUpperCase() +
                    " LOST\nPLAYER " + secondPlayer.getName().toUpperCase() + " WON\nGAME OVER";
        }

        System.out.println(secondPlayer.status() + "\n");
        makeAMove(secondPlayer);
        System.out.println("\n" + secondPlayer.status());
        System.out.println(board.field(secondPlayer));
        in.nextLine();
        if (secondPlayer.isBankrupt()) {
            return "\nPLAYER " + secondPlayer.getName().toUpperCase() +
                    " LOST\nPLAYER " + firstPlayer.getName().toUpperCase() + " WON\nGAME OVER";
        }

        return play();
    }

    /**
     * Метод хода состоит из вызова методов броска костей и затем
     * взаимодействия игрока с клеткой.
     *
     * @param player игрок, совершающий ход.
     */
    static void makeAMove(Player player) {
        roll(player);
        cellInteraction(player);
    }

    /**
     * Метод броска костей: генерируются два числа и
     * позиция игрока изменяется на их сумму.
     *
     * @param player игрок, бросающий кости.
     */
    static void roll(Player player) {
        int dice1 = ThreadLocalRandom.current().nextInt(1, 6);
        int dice2 = ThreadLocalRandom.current().nextInt(1, 6);
        System.out.println("Player " + player.getName()
                + " rolls the dice...\nIt's "
                + dice1 + " and " + dice2 + "!\n"
                + player.move(dice1 + dice2));
    }

    /**
     * Метод взаимодействия игрока с клеткой. Выводится сообщение клетки,
     * затем, если требуется ответ, если игрок бот, то вызывается его метод
     * принятия решений, иначе считывается консоль, вызывается метод завершения
     * хода на клетке, если же диалога нет, просто вызывается метод завершения
     * хода на клетке, и если клетка была такси, вызывается метод взаимодействия
     * игрока с новой клеткой.
     *
     * @param player игрок, сделавший ход.
     */
    static void cellInteraction(Player player) {
        Cell playersPosition = board.cellAtPosition(player.getPosition());
        System.out.println(playersPosition.message(player));

        if (playersPosition.answerNeeded(player)) {
            if (player instanceof BotPlayer) {
                String answer = ((BotPlayer) player).makeDecision();
                System.out.println("Bot decides: " + answer);
                System.out.println(playersPosition.stepOnCell(player, answer));
            } else {
                do {
                    getInput(playersPosition, player);
                } while (redoInput);
            }
        } else if (!((player instanceof BotPlayer) &&
                (playersPosition instanceof BankCell))) {
            System.out.println(playersPosition.stepOnCell(player));
            if (playersPosition instanceof TaxiCell) {
                cellInteraction(player);
            }
        }
    }

    /**
     * Метод для считывания ответа пользователя из консоли.
     * Если ответ неверный для клетки, ввод повторяется.
     *
     * @param playersPosition клетка, с которой происходит
     *                        взаимодействие.
     * @param player          игрок, взаимодействующий с клеткой.
     */
    static void getInput(Cell playersPosition, Player player) {
        try {
            String input = in.nextLine();
            System.out.println(playersPosition.stepOnCell(player, input));
            redoInput = false;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            redoInput = true;
        }
    }

    /**
     * Метод для парсинга аргументов командной строки.
     * Если что-то неверно или лежит в неправильных
     * границах, игра не начинается.
     *
     * @param args аргументы командной строки.
     */
    static void inputArgs(String[] args) {
        height = Integer.parseInt(args[0]);
        width = Integer.parseInt(args[1]);
        money = Integer.parseInt(args[2]);

        if (height > 30 || height < 6
                || width > 30 || width < 6
                || money > 15000 || 500 > money) {
            System.out.println("Input parameters are in wrong bounds! ");
            play = false;
        }
    }
}
