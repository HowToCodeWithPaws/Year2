/**
 * @author <a href="mailto:ndzubareva@edu.hse.ru"> Zubareva Natalia</a>
 */

package monopoly.monopoly2;

import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для игрового поля с статическими полями
 * ширины и высоты, массивом клеток поля,
 * коэффициентами долга, кредитования и штрафа.
 */
public class Board {
    private static int width, height;
    private Cell[] cells;
    double debtCoeff, credCoeff, penaCoeff;

    /**
     * Конструктор с параметрами ширины и высоты поля.
     * Создается массив клеток соответствущего размера,
     * задаются коэффициенты,задаются статические коэффициенты,
     * вызывается метод создания игрового поля и его вывод в консоль.
     *
     * @param height_ высота поля.
     * @param width_  ширина поля.
     */
    public Board(int height_, int width_) {
        height = height_;
        width = width_;
        cells = new Cell[2 * (height + width - 2)];

        debtCoeff = ThreadLocalRandom.current().nextDouble(1, 3);
        credCoeff = ThreadLocalRandom.current().nextDouble(0.002, 0.2);
        penaCoeff = ThreadLocalRandom.current().nextDouble(0.01, 0.1);

        BankCell.setCoeff(credCoeff, debtCoeff);
        PenaltyCell.setCoeff(penaCoeff);

        System.out.println(create());
    }

    /**
     * Метод создания игрового поля, создается нужное количество
     * пустых клеток, штрафных клеток, клеток такси, офисов банков,
     * остальные заполняются магазинами с случайно сгенерированными
     * характеристиками.
     *
     * @return сообщение о создании поля.
     */
    private String create() {
        cells[0] = new EmptyCell(0, 0);
        cells[makeXYToN(width - 1, 0)] = new EmptyCell(width - 1, 0);
        cells[makeXYToN(width - 1, height - 1)] =
                new EmptyCell(width - 1, height - 1);
        cells[makeXYToN(0, height - 1)] = new EmptyCell(0, height - 1);

        for (int i = 0; i < 4; ++i) {
            putACell(i, 2, "penaltyCell");
            putACell(i, 2, "taxiCell");
            putACell(i, 1, "bankCell");
        }

        for (int i = 0; i < cells.length; ++i) {
            double priceN = ThreadLocalRandom.current().nextDouble(50, 500);
            double comK = ThreadLocalRandom.current().nextDouble(0.5, 0.9) * priceN;
            double impC = ThreadLocalRandom.current().nextDouble(0.1, 2);
            double comC = ThreadLocalRandom.current().nextDouble(0.1, 1);

            if (cells[i] == null) cells[i] = new ShopCell(makeNToXY(i)[0],
                    makeNToXY(i)[1], priceN, comK, impC, comC);
        }

        return info();
    }

    /**
     * Метод для установки нужного количества клеток определенного типа
     * на линию с созданием параметров для них. Положение генерируется случайно,
     * в зависимости от номера линии рассчитываются границы координат клеток.
     *
     * @param line          номер линии, на которой создаются клетки.
     * @param numberOfCells количество нужных клеток.
     * @param type          тип нужных клеток.
     */
    private void putACell(int line, int numberOfCells, String type) {
        for (int i = 0; i < numberOfCells; ++i) {

            int bound = line % 2 == 0 ? width - 2 : height - 2;
            int rand = ThreadLocalRandom.current().nextInt(1, bound);
            int first = line % 2 == 0 ? rand : line % 4 == 1 ? width - 1 : 0;
            int second = line % 2 == 0 ? line % 4 == 0 ? 0 : height - 1 : rand;

            switch (type) {
                case "bankCell":
                    cells[makeXYToN(first, second)] = new BankCell(first, second);
                    break;
                case "taxiCell":
                    cells[makeXYToN(first, second)] = new TaxiCell(first, second);
                    break;
                case "penaltyCell":
                    cells[makeXYToN(first, second)] = new PenaltyCell(first, second);
                    break;
            }
        }
    }

    /**
     * Метод для перевода координат из пары чисел - х и у
     * в одно число - порядковый номер клетки на поле.
     *
     * @param x координата х.
     * @param y координата у.
     * @return координата - порядковый номер.
     */
    private static int makeXYToN(int x, int y) {
        if (y == 0) return x;
        if (x == width - 1) return width - 1 + y;
        if (y == height - 1) return height + 2 * width - 3 - x;
        return 2 * (height + width - 2) - y;
    }

    /**
     * Метод для перевода координаты - порядкового номера
     * в координаты х и у.
     *
     * @param index порядковый номер клетки.
     * @return координаты х и у клетки.
     */
    protected static int[] makeNToXY(int index) {
        if (index < width) return new int[]{index, 0};
        if (index < height + width - 1) return new int[]{width - 1, index - width + 1};
        if (index < 2 * width + height - 2) return
                new int[]{height + 2 * width - 3 - index, height - 1};
        return new int[]{0, 2 * (height + width - 2) - index};
    }

    /**
     * Метод для вывода коэффициентов, сгенерированных на игру.
     *
     * @return сообщение о коэффициентах.
     */
    private String info() {
        return "\nFOR THIS GAME\nPenalty coefficient = " +
                String.format("%.2f", penaCoeff) +
                "\nDebt coefficient = " + String.format("%.2f", debtCoeff) +
                "\nCredit coefficient = " + String.format("%.2f", credCoeff);
    }

    /**
     * Метод создания текстовой визуализации игрового поля
     * в зависимости от того, какой игрок на него смотрит.
     * Последовательно вызываются методы, генерирующие строки
     * для координат и первой линии, затем серединной части поля,
     * затем нижней линии поля.
     *
     * @param player игрок, для которого происходит отображение.
     * @return текстовый вид поля.
     */
    public String field(Player player) {

        String field = firstTwoLines(player);

        field += middleLines(player);

        field += lastLine(player);

        return field;
    }

    /**
     * Метод для создания первой линии координатной сетки
     * и первой линии поля. В большом количестве использованы
     * string joinerы, потому что это лучше, чем += к строкам.
     * Отдельно вызывается метод создания этих строк в длинном
     * случае, потому что там другое форматирование.
     *
     * @param player игрок, для которого отображается поле.
     * @return первые две строки вида поля.
     */
    String firstTwoLines(Player player) {
        String coordinates = height < 11 ? "  " : "   ",
                cellsLine = height < 11 ? "0 " : " 0 ";
        StringJoiner joinerField, joinerIndex;

        if (width < 10) {
            joinerIndex = new StringJoiner(" ");
            joinerField = new StringJoiner(" ");
            for (int i = 0; i < width; ++i) {
                joinerIndex.add("" + i);
                joinerField.add("" + cells[i].getSymbol(player));
            }

            return coordinates + joinerIndex.toString() + "\n"
                    + cellsLine + joinerField.toString();
        } else {
            return firstLinesLong(player);
        }
    }

    /**
     * Метод для создания первых двух строк при ширине поля больше 10.
     *
     * @param player игрок, для которого отображается поле.
     * @return строка с первыми двумя строками отображения поля.
     */
    String firstLinesLong(Player player) {
        String coordinates = height < 11 ? "  " : "   ",
                cellsLine = height < 11 ? "0 " : " 0 ";
        StringJoiner joinerIndex = new StringJoiner("  ");
        StringJoiner joinerField = new StringJoiner("  ");

        for (int i = 0; i < 9; ++i) {
            joinerIndex.add("" + i);
            joinerField.add("" + cells[i].getSymbol(player));
        }
        coordinates += joinerIndex.toString() + "  ";
        joinerIndex = new StringJoiner(" ");
        for (int i = 9; i < width; ++i) {
            joinerIndex.add("" + i);
            joinerField.add("" + cells[i].getSymbol(player));
        }

        return coordinates + joinerIndex.toString() + "\n"
                + cellsLine + joinerField.toString();
    }

    /**
     * Метод для создания средней части поля - второй и четвертой
     * линий соответственно, а также индексов координат слева и
     * нужного рассчитываемого числа пробелов в центре.
     *
     * @param player игрок, для которого отображается поле.
     * @return строка с средней частью поля.
     */
    String middleLines(Player player) {
        String output = "\n";
        String between = width < 10 ? " ".repeat(2 * width - 3)
                : " ".repeat(3 * width - 4);
        StringJoiner joiner = new StringJoiner("\n");

        if (height < 11) {
            for (int i = 1; i < height - 1; ++i) {
                joiner.add("" + i + " " + cells[makeXYToN(0, i)].getSymbol(player) +
                        between + cells[makeXYToN(width - 1, i)].getSymbol(player));
            }
        } else {
            for (int i = 1; i < 10; ++i) {
                joiner.add(" " + i + " " + cells[makeXYToN(0, i)].getSymbol(player)
                        + between + cells[makeXYToN(width - 1, i)].getSymbol(player));
            }
            for (int i = 10; i < height - 1; ++i) {
                joiner.add(i + " " + cells[makeXYToN(0, i)].getSymbol(player)
                        + between + cells[makeXYToN(width - 1, i)].getSymbol(player));
            }
        }

        return output + joiner.toString() + "\n";
    }

    /**
     * Метод для создания строки с третьей линией поля.
     *
     * @param player игрок, для которого поле отображается.
     * @return строка с нижней линией.
     */
    String lastLine(Player player) {
        String delimiter = width < 10 ? " " : "  ";
        StringJoiner joiner = new StringJoiner(delimiter);

        int index = height - 1;

        if (width < 10) {
            for (int i = 0; i < width; ++i) {
                joiner.add("" + cells[makeXYToN(i, height - 1)].getSymbol(player));
            }
        } else {
            for (int i = 0; i < 9; ++i) {
                joiner.add("" + cells[makeXYToN(i, height - 1)].getSymbol(player));
            }
            for (int i = 9; i < width; ++i) {
                joiner.add("" + cells[makeXYToN(i, height - 1)].getSymbol(player));
            }
        }

        return "" + index + " " + joiner.toString();
    }

    /**
     * Метод для получения клетки на итой позиции,
     * нужен для доступа к клетке извне без непосредственного
     * взаимодействия с массивом.
     *
     * @param i порядковый номер клетки.
     * @return клетка на итой позиции.
     */
    public Cell cellAtPosition(int i) {
        return cells[i % cells.length];
    }

    /**
     * Метод, возвращающий размеры поля.
     *
     * @return количество клеток поля.
     */
    public static int size() {
        return 2 * (height + width - 2);
    }
}
