package the_cart_story.doings;

import org.junit.jupiter.api.Test;
import the_cart_story.things.Animal;
import the_cart_story.things.Cart;

import java.util.Timer;

import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    @Test
    void parseInputToCart() {
        String input1 = "";
        String input2 = "23.34";
        String input3 = "23,34";
        String []  input4 = new String[] {"23.34","1"};
        String [] input5 = new String[]{"adfg"," 1"};
        String [] input6 = new String[]{"adfg"," 1vb"};
        assertEquals((new Cart(0, 0)).toString(), Program.parseInputToCart(new String[]{input1}).toString());
        assertEquals((new Cart(23.34, 0)).toString(), Program.parseInputToCart(new String[]{input2}).toString());
        assertEquals((new Cart(0, 0)).toString(), Program.parseInputToCart(new String[]{input3}).toString());
        assertEquals((new Cart(23.34, 1)).toString(), Program.parseInputToCart(input4).toString());
        assertEquals((new Cart(0, 1)).toString(), Program.parseInputToCart(input5).toString());
        assertEquals((new Cart(0, 0)).toString(), Program.parseInputToCart(input6).toString());
    }

    @Test
    void schedule() {
        Cart cart = new Cart(0,0);
        Boolean timeToStop = false;
        Animal an1 = new Animal(cart, "Swan", Math.toRadians(60),
                timeToStop, Program.ANSI_SWAN);
        Animal an2 = new Animal(cart, "Cancer", Math.toRadians(180),
                timeToStop, Program.ANSI_CANCER);
        Animal an3 = new Animal(cart, "Pike", Math.toRadians(300),
                timeToStop, Program.ANSI_PIKE);
        Timer timer = new Timer();
        assertDoesNotThrow(()->Program.schedule(timer, an1, an2, an3, cart));
    }

    @Test
    void main() {
        assertDoesNotThrow(()->Program.main(new String[]{"--test"}));
    }
}