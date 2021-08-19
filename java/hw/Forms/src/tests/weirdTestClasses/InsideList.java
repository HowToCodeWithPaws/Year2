package weirdTestClasses;

import annotations.*;

import java.util.*;

/***
 * Класс для вложенности и листов. Здесь лежат многовложенные листы и сет,
 * что позволяет проверить размах коллекционности.
 */
@Constrained
class InsideList {
    @NotNull
    @NotEmpty
    private List<@NotNull List<@NotBlank @NotEmpty @Size(min = 3, max = 10) String>> listStrings = new ArrayList<>() {{
        add(new LinkedList<>() {{
            add("");
            add("я");
            add("у");
            add("с т а л ");
        }});
        add(new LinkedList<>() {{
            add("2:08");
            add("   ");
            add("aaaaaaaaaaaa");
            add("ыы");
        }});
    }};

    @NotNull
    @NotEmpty
    private Set<@NotNull Set<@Positive @InRange(min = -1, max = 2) Integer>> set = new HashSet<>() {{
        add(new HashSet<>() {{
            add(100);
            add(1);
            add(0);
        }});
    }};
}
