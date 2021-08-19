package the_cart_story.things;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void run() {
        Cart cart = new Cart(0, 0);
        Boolean timeToStop = false;
        Animal animal = new Animal(cart, "Miserable Soul", 30, timeToStop, "\u001B[0m");

            assertDoesNotThrow(() -> animal.run());
        assertFalse(animal.isAnimalAlive());
        assertNotEquals(0, cart.getXCoordinate());
        assertNotEquals(0, cart.getYCoordinate());
    }

    @Test
    void pull() throws InterruptedException {
        Cart cart = new Cart(0, 0);
        Boolean timeToStop = false;
        Animal animal = new Animal(cart, "Miserable Soul", 30, timeToStop, "\u001B[0m");
        synchronized (timeToStop) {
            assertDoesNotThrow(() -> animal.pull());
        }
        assertNotEquals(0, cart.getXCoordinate());
        assertNotEquals(0, cart.getYCoordinate());
    }

    @Test
    void isAnimalAlive() throws InterruptedException {
        Cart cart = new Cart(0, 0);
        Animal animal = new Animal(cart, "Miserable Soul", 30, false, "\u001B[0m");
        assertDoesNotThrow(() -> animal.isAnimalAlive());
        assertTrue(animal.isAnimalAlive());
        Thread.sleep(25000);
        assertTrue(animal.isAnimalAlive());
        animal.start();
        Thread.sleep(25000);
        assertFalse(animal.isAnimalAlive());
        assertThrows(Exception.class,()->{
            animal.start();animal.interrupt();});
    }
}