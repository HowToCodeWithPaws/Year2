package weirdTestClasses;

import annotations.Constrained;
import annotations.InRange;
import annotations.NotBlank;
import annotations.NotNull;

/***
 * Класс из примеров условия - форма посетителя.
 */
@Constrained
public class GuestForm {
    @NotNull
    @NotBlank
    private String firstName;
    @NotBlank
    @NotNull
    private String lastName;
    @InRange(min = 0, max = 200)
    private int age;


    public GuestForm(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}