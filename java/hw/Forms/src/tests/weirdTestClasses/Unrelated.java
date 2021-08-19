package weirdTestClasses;

import annotations.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/***
 * Класс из примеров, в который были добавлены многочисленные
 * поля для проверки всего на свете - правильных и неправильных
 * аннотаций, строк, чисел, коллекций. Они публичные, потому что
 * метод проверки в классах ошибок не умеет доставать приватные поля.
 * В любом случае приватность проверяется на глобальных примерах в
 * тестах валидатора.
 */
public class Unrelated {
    @Positive
    private int x;

    @Size(min = 6, max = 3)
    public String kstring = "ffff";
    @Size(min = 1, max = 5)
    public String str;
    @NotEmpty
    public String empty = "";
    @NotBlank
    public String blank = "    ";
    @AnyOf({"a", "b", "c"})
    @NotBlank
    public String string1 = "a";
    @AnyOf({"a", "b", "c"})
    public String string2 = "what";

    @NotNull
    @Negative
    @InRange(min = 1, max = 20)
    public int y;
    @Positive
    @InRange(min = 1, max = 20)
    public short z;
    @InRange(min = 6, max = 3)
    public int k = 5;

    @Size(min = 1, max = 5)
    public Map<String, String> map;
    @NotNull
    public LinkedList<Integer> list = null;

    public Unrelated(String str) {
        this.str = str;
        z = 20;
        y = -5;
        map = new HashMap<String, String>();
        map.put("Yee", "Haw");
    }

    public Unrelated(int x) {
        this.x = x;
    }
}