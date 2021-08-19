package the_cart_story.things;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void setXCoordinate() {
        Cart cart = new Cart(100,100);
        assertEquals(cart.getXCoordinate(), 100);
        assertDoesNotThrow(()->cart.setXCoordinate(-100));
        assertEquals(cart.getXCoordinate(), -100);
    }

    @Test
    void setYCoordinate() {
        Cart cart = new Cart(100,100);
        assertEquals(cart.getYCoordinate(), 100);
        assertDoesNotThrow(()->cart.setYCoordinate(-100));
        assertEquals(cart.getYCoordinate(), -100);
    }

    @Test
    void getXCoordinate() {
        Integer integer = 1000;
        Cart cart = new Cart(integer,100);
        assertEquals((int) cart.getXCoordinate(), integer);
        assertEquals(cart.getXCoordinate(), 1000);
    }

    @Test
    void getYCoordinate() {
        Integer integer = 1000;
        Cart cart = new Cart(100,integer);
        assertEquals((int) cart.getYCoordinate(), integer);
        assertEquals(cart.getYCoordinate(), 1000);
    }

    @Test
    void testToString() {
        Cart cart = new Cart(15.111444555,-100);
        assertDoesNotThrow(()->cart.toString());
        assertEquals("the cart is at (15,111, -100,000)", cart.toString());
    }
}