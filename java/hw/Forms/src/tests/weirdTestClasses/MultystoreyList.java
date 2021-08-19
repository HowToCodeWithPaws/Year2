package weirdTestClasses;

import annotations.*;

import java.util.*;

/***
 * Мой класс для проверки функционала библиотеки на коллекциях,
 * в том числе вложенных, в том числе сетах и мапах.
 */
@Constrained
public class MultystoreyList {

    @NotNull
    List<@NotNull Map<@NotBlank String, @Positive @InRange(min = -2, max = 2) Integer>>
            maps1 = new ArrayList<>() {{
        add(new HashMap<>() {{
            put("в", 1);
            put("ш", -1);
            put("э", 10);
            put("    ", 0);
        }});
    }};

    @NotNull
    List<@NotNull List<List<@Positive @Negative Integer>>>
            list2 = new ArrayList<>() {{
        add(new ArrayList<>() {{
            add(new LinkedList<>() {{
                add(1);
                add(-1);
                add(0);
            }});
        }});
        add(new ArrayList<>() {{
            add(new LinkedList<>() {{
                add(null);
            }});
        }});
        add(null);
    }};

    @NotNull
    InsideList inside = new InsideList();
}