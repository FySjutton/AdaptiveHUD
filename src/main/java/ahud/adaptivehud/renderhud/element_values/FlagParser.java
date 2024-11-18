package ahud.adaptivehud.renderhud.element_values;

import ahud.adaptivehud.AdaptiveHudRegistry;
import ahud.adaptivehud.renderhud.element_values.inbuilt_flags.DefaultFlags;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FlagParser {
    private final AdaptiveHudRegistry registry = new AdaptiveHudRegistry();
    private final DefaultFlags flagParser = new DefaultFlags();

    public String parseFlags(String value, HashMap<String, String[]> flags) {
        for (Map.Entry<String, String[]> entry : flags.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            Method flag = registry.loadFlag(key);
            try {
                Object[] parameters = new Object[2];
                parameters[0] = value;
                parameters[1] = values;
                value = String.valueOf(flag.invoke(flagParser, parameters));
            } catch (Exception ignored) {}
        }
        return value;
    }
}