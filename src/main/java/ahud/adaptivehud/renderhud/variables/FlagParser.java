package ahud.adaptivehud.renderhud.variables;

import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

public class FlagParser {
    private FlagTools tools = new FlagTools();

    public String parseFlags(String value, HashMap<String, String[]> flags) {
        for (Map.Entry<String, String[]> entry : flags.entrySet()) {
            String K = entry.getKey();
            String[] Vs = entry.getValue();

            if (Vs != null) {
                try {
                    switch (K) {
                        case "round" -> value = tools.roundNum(Float.parseFloat(value), Integer.parseInt(Vs[0]));
                        case "replace" -> value = value.replaceAll(Vs[0], Vs[1]);
                        case "split" -> value = value.split(Vs[0])[Integer.parseInt(Vs[1])];
                        case "substring" -> value = Vs.length == 2 ? value.substring(Integer.parseInt(Vs[0]), Integer.parseInt(Vs[1])) : value.substring(Integer.parseInt(Vs[0]));
                        case "contains" -> value = String.valueOf(value.contains(Vs[0]));
                        case "charat" -> value = String.valueOf(value.charAt(Integer.parseInt(Vs[0])));
                    }
                } catch (Exception ignored) {}
            } else {
                switch (K) {
                    case "uc" -> value = value.toUpperCase();
                    case "lc" -> value = value.toLowerCase();
                    case "tc" -> value = WordUtils.capitalizeFully(value);
                    case "nd" -> value = value.replaceAll("[-_]", " ");
                    case "sh" -> value = value.split(":")[1];
                    case "length" -> value = String.valueOf(value.length());
                }
            }
        }
        return value;
    }
}