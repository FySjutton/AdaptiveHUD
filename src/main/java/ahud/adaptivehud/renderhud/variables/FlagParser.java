package ahud.adaptivehud.renderhud.variables;

import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

public class FlagParser {
    private FlagTools tools = new FlagTools();

    public String parseFlags(String value, HashMap<String, String> flags) {
        for (Map.Entry<String, String> entry : flags.entrySet()) {
            String K = entry.getKey();
            String V = entry.getValue();

            if (V != null) {
                switch (K) {
                    case "round" -> {
                        try {
                            value = tools.roundNum(Float.parseFloat(value), Integer.parseInt(V));
                        } catch (Exception ignored) {}
                    }
                }
            } else {
                switch (K) {
                    case "uc" -> value = value.toUpperCase();
                    case "lc" -> value = value.toLowerCase();
                    case "tc" -> value = WordUtils.capitalizeFully(value);
                    case "nd" -> value = value.replaceAll("[-_]", " ");
                    case "sh" -> value = value.split(":")[1];
                }
            }
        }
        return value;
    }
}
