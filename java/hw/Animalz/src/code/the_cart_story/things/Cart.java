package the_cart_story.things;

/**
 * Класс для тележки - это объект, который описывается двумя
 * вещественными координатами, у которых есть сеттеры и геттеры,
 * также есть метод, возвращающий строку с текущим положением тележки.
 */
public class Cart {
    private double xCoordinate, yCoordinate;

    /**
     * Конструктор с параметрами координат.
     *
     * @param x координата х.
     * @param y координата у.
     */
    public Cart(double x, double y) {
        xCoordinate = x;
        yCoordinate = y;
    }

    /**
     * Сеттер координаты х - устанавливает вещественное значение.
     *
     * @param newX новое значение, которое мы устанавливаем.
     */
    public void setXCoordinate(double newX) {
        xCoordinate = newX;
    }

    /**
     * Сеттер координаты у - устанавливает вещественное значение.
     *
     * @param newY новое значение, которое мы устанавливаем.
     */
    public void setYCoordinate(double newY) {
        yCoordinate = newY;
    }

    /**
     * Геттер координаты х.
     *
     * @return получаем вещественное значение х.
     */
    public double getXCoordinate() {
        return xCoordinate;
    }

    /**
     * Геттер координаты н.
     *
     * @return получаем вещественное значение у.
     */
    public double getYCoordinate() {
        return yCoordinate;
    }

    /**
     * Переопределенный метод, возвращающий информацию о
     * текущем положении тележки. Вещественные координаты
     * выводятся с точностью до трех знаков после запятой.
     *
     * @return строка с текущим положением тележки.
     */
    @Override
    public String toString() {
        return "the cart is at (" + String.format("%.3f", xCoordinate) + ", "
                + String.format("%.3f", yCoordinate) + ")";
    }
}
