package the_cart_story.doings;

import the_cart_story.things.Animal;
import the_cart_story.things.Cart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс программы, в котором происходит вроде как вся работа:
 * считываются входные данные командной строки, создаются тележка и существа,
 * запускается работа по перетягиванию, затем все завершается.
 * Тут есть поля для флагов начала и приостановки, цветов (потому что избранная
 * дополнительная функция - покраска консоли), форматтеры времени.
 */
public class Program {
    private static Boolean timeToStop = false;
    private static boolean starting = true;
    public static boolean needColours = false;
    public static String ANSI_RESET = "\u001B[0m";
    public static String ANSI_SWAN = "\u001B[96m";
    public static String ANSI_CANCER = "\u001B[91m";
    public static String ANSI_PIKE = "\u001B[92m";
    public static String ANSI_CART = "\u001B[35m";
    private static final String pattern = "HH:mm:ss";
    private static final SimpleDateFormat format = new SimpleDateFormat(pattern);

    /**
     * Метод для парсинга входных параметров в координаты телеги и
     * создания телеги: если у нас что-то не выходит распарсить, или
     * этого нет, мы выводим сообщение и задаем эту координату равной нулю.
     *
     * @param args аргументы командной строки.
     * @return новая телега, созданная по координатам.
     */
    public static Cart parseInputToCart(String[] args) {
        double xCoord = 0, yCoord = 0;
        if (args.length > 0) {
            try {
                xCoord = Double.parseDouble(args[0]);
            } catch (Exception e) {
                System.out.println("A thing went wrong with your " +
                        "input of x coordinate: "
                        + e.getMessage() + "\nWe'll make it 0 instead");
            }
        }

        if (args.length > 1) {
            try {
                yCoord = Double.parseDouble(args[1]);
            } catch (Exception e) {
                System.out.println("A thing went wrong with your " +
                        "input of y coordinate: "
                        + e.getMessage() + "\nWe'll make it 0 instead");
            }
        }

        return new Cart(xCoord, yCoord);
    }

    /**
     * Метод для настройки действий таймера: каждые 2 секунды
     * мы тормозим вывод животных, чтобы вывести сообщение о расположении
     * тележки. Также таймер занимается тем, что в первый раз после вывода
     * начального положения тележки запускает всех животных. Также он выполняет
     * проверку на живость животных и выключается с выводом финального
     * сообщения, когда все животные умирают.
     *
     * @param timerMessages таймер, который мы настраиваем.
     * @param swan          существо-лебедь.
     * @param cancer        существо-рак.
     * @param pike          существо-щука.
     * @param cart          тележка.
     */
    public static void schedule(Timer timerMessages, Animal swan, Animal cancer, Animal pike, Cart cart) {
        timerMessages.schedule(new TimerTask() {
            @Override
            public void run() {
                timeToStop = true;
                synchronized (cart) {
                    synchronized (timeToStop) {
                        System.out.println(Program.ANSI_CART + format.format(new Date())
                                + " " + cart.toString() + " " + Program.ANSI_RESET);
                        timeToStop = false;
                    }
                }

                if (starting) {
                    swan.start();
                    pike.start();
                    cancer.start();
                    starting = false;
                }

                if (!(swan.isAnimalAlive() || pike.isAnimalAlive() || cancer.isAnimalAlive())) {
                    System.out.println("All animals are dead! This is what forced labour leads to!");
                    timerMessages.cancel();
                }
            }
        }, 0, 2000);
    }

    /**
     * Это абсолютно ненужный метод для разговора с пользователем:
     * хочет ли пользователь включить цвета. Это довольно милая доп фича,
     * но она не работает в консолях виндоус, поэтому я рассчитываю на честность.
     * Передаем аргументы командной строки, потому что apparently в джаве нельзя
     * тестировать ввод из терминала, а тест мейна надо как-то морально подготовить.
     * Для этого тестировать мы всегда будем с флагом --test и цвета включать не будем.
     *
     * @param args аргументы запуска.
     */
    private static void askAboutColours(String[] args) {
        if (args.length > 0 && args[0].equals("--test")) {
            return;
        }
        System.out.println("As a cool feature, you can enable colours in console," +
                "\nbut only if you are operating on Linux or in INTELLIJ (not in the terminal)." +
                "\nIf this is true and you wish to enable colours, press Y, otherwise (and pls be honest) DONT");

        try {
            if (System.in.readNBytes(1)[0] == 'Y') {
                needColours = true;
                System.out.println("Cool! Enabling colours!");
            }
        } catch (java.io.IOException e) {
            System.out.println("I'll take it as a no");
        }

        if (!needColours) {
            ANSI_CANCER = ANSI_CART = ANSI_PIKE = ANSI_RESET = ANSI_SWAN = "";
        }
    }

    /**
     * Точка входа в приложение, здесь мы создаем существ,
     * тележку (с вызовом метода чтения аргументов командной строки) и
     * таймер, вызываем метод, создающий расписание таймера.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        askAboutColours(args);
        System.out.println(Program.ANSI_CART
                + "~~This is a contemporary illustration of a well-known " +
                "story about teamwork~~"
                + Program.ANSI_RESET);
        Cart cart = parseInputToCart(args);
        Animal swan = new Animal(cart, "Swan", Math.toRadians(60),
                timeToStop, Program.ANSI_SWAN);
        Animal cancer = new Animal(cart, "Cancer", Math.toRadians(180),
                timeToStop, Program.ANSI_CANCER);
        Animal pike = new Animal(cart, "Pike", Math.toRadians(300),
                timeToStop, Program.ANSI_PIKE);

        Timer timerMessages = new Timer();
        schedule(timerMessages, swan, cancer, pike, cart);
    }
}
