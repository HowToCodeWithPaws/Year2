package the_cart_story.things;

import the_cart_story.doings.Program;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс животного, которое умеет тянуть тележку.
 * Наследуется от потока, поэтому может запускаться.
 * Для этого у каждого животного есть ссылка на тележку,
 * сила и угол тяги, имя, время начала тяги (для смерти
 * через 25 секунд), ссылка на параметр остановки, также
 * поле характеристики, живо ли еще животное и параметр цвета.
 */
public class Animal extends Thread {
    private final Cart cart;
    private final double strength;
    private final double degree;
    private final String name;
    private final long begin;
    private final Boolean timeToStop;
    private final String colour;
    private boolean alive;

    /**
     * Конструктр с параметрами, задающий параметры животного.
     * Некоторые из них передаются (ниже), остальные же (сила)
     * генерируются случайно потокобезопасным образом.
     * Начинается отсчет жизни животного, задается значение его живости,
     * выводится сообщение о его появлении.
     *
     * @param cart       ссылка на тележку.
     * @param name       имя животного.
     * @param degree     градус(в радианах), под которым животное тянет тележку.
     * @param timeToStop переменная для остановки.
     * @param colour     цвет этого животного.
     */
    public Animal(Cart cart, String name, double degree, Boolean timeToStop, String colour) {
        this.cart = cart;
        this.name = name;
        this.degree = degree;
        this.timeToStop = timeToStop;
        this.colour = colour;
        this.strength = ThreadLocalRandom.current().nextDouble(1, 10);
        begin = System.currentTimeMillis();
        alive = true;
        System.out.println("Congratulations! It's a " + colour + name + Program.ANSI_RESET
                + "! Its power is " + String.format("%.3f", strength));
    }

    /**
     * Метод для запуска потока, пока животному не пора
     * умирать, если не нужно сделать остановку для вывода
     * текущего положения тележки (синхронизация по переменной),
     * животное будет тянуть тележку до тех пор, пока не умрет.
     */
    @Override
    public void run() {
        while (System.currentTimeMillis() - begin < 25000) {
            synchronized (timeToStop) {
                if (!timeToStop) {
                    pull();
                }
            }
        }
        System.out.println("I'm " + colour + name + Program.ANSI_RESET
                + " and i'm so tired that i'm dying");
        alive = false;
    }

    /**
     * Метод для того, чтобы тянуть тележку:
     * животное синхронизируется по тележке, чтобы избежать конфликтов,
     * происходит расчет новых координат, выводится сообщение о сдвиге,
     * тележка двигается. После этого животное уходит в сон на случайное
     * время.
     */
    public synchronized void pull() {

        synchronized (cart) {
            double resX = cart.getXCoordinate() + strength * Math.cos(degree);
            double resY = cart.getYCoordinate() + strength * Math.sin(degree);

            System.out.println("I'm animal " + colour + name + Program.ANSI_RESET
                    + " and i'm pulling the cart!!! Now I'm tired and I want to go to bed");
            cart.setXCoordinate(resX);
            cart.setYCoordinate(resY);
        }
        try {
            timeToStop.wait(ThreadLocalRandom.current().nextInt(1000, 5000));
        } catch (InterruptedException e) {
            alive = false;
            System.out.println("InterruptedException: " + e);
        }
    }

    /**
     * @return характеристика того, живо ли еще животное.
     */
    public boolean isAnimalAlive() {
        return alive;
    }
}
