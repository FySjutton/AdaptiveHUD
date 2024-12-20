package ahud.adaptivehud.renderhud.element_values.inbuilt_flags;

import ahud.adaptivehud.AdaptiveHudRegistry;
import ahud.adaptivehud.renderhud.element_values.FlagTools;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class DefaultFlags {
    private final FlagTools tools = new FlagTools();

    public void loadNonValueFlags() {
        AdaptiveHudRegistry registry = new AdaptiveHudRegistry();
        Method[] methods = DefaultFlags.class.getDeclaredMethods();
        HashMap<String, Method> flags = new HashMap<>();

        for (Method x : methods) {
            String name = x.getName();
            if (!name.equals("loadNonValueFlags")) {
                flags.put(name, x);
            }
        }

        registry.registerFlag("uppercase", flags.get("uppercase"), false);
        registry.registerFlag("uc", flags.get("uppercase"), false);
        registry.registerFlag("lowercase", flags.get("lowercase"), false);
        registry.registerFlag("lc", flags.get("lowercase"), false);
        registry.registerFlag("titlecase", flags.get("titlecase"), false);
        registry.registerFlag("tc", flags.get("titlecase"), false);
        registry.registerFlag("no_dashes", flags.get("no_dashes"), false);
        registry.registerFlag("nd", flags.get("no_dashes"), false);
        registry.registerFlag("short", flags.get("turn_short"), false);
        registry.registerFlag("length", flags.get("length"), false);
        registry.registerFlag("len", flags.get("length"), false);
        registry.registerFlag("round", flags.get("round"), false);
        registry.registerFlag("replace", flags.get("replace"), false);
        registry.registerFlag("split", flags.get("split"), false);
        registry.registerFlag("substring", flags.get("substring"), false);
        registry.registerFlag("contains", flags.get("contains"), false);
        registry.registerFlag("charat", flags.get("charat"), false);
    }

    public String uppercase(String value, List<String> values) {
        return value.toUpperCase();
    }

    public String lowercase(String value, List<String> values) {
        return value.toLowerCase();
    }

    public String titlecase(String value, List<String> values) {
        return WordUtils.capitalizeFully(value);
    }

    public String no_dashes(String value, List<String> values) {
        return value.replaceAll("[-_]", " ");
    }

    public String turn_short(String value, List<String> values) {
        return value.split(":")[1];
    }

    public String length(String value, List<String> values) {
        return String.valueOf(value.length());
    }

    public String round(String value, List<String> values) {
        return tools.roundNum(Float.parseFloat(value), Integer.parseInt(values.getFirst()));
    }

    public String replace(String value, List<String> values) {
        return value.replaceAll(values.getFirst(), values.get(1));
    }

    public String split(String value, List<String> values) {
        return value.split(values.getFirst())[Integer.parseInt(values.get(1))];
    }

    public String substring(String value, List<String> values) {
        return values.size() == 2 ? value.substring(Integer.parseInt(values.getFirst()), Integer.parseInt(values.get(1))) : value.substring(Integer.parseInt(values.getFirst()));
    }

    public String contains(String value, List<String> values) {
        return String.valueOf(value.contains(values.getFirst()));
    }

    public String charat(String value, List<String> values) {
        return String.valueOf(value.charAt(Integer.parseInt(values.getFirst())));
    }
}
