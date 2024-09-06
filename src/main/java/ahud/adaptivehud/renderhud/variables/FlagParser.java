package ahud.adaptivehud.renderhud.variables;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlagParser {
    private FlagTools tools = new FlagTools();

    public String parseFlags(String value, HashMap<String, String[]> flags) {
        for (Map.Entry<String, String[]> entry : flags.entrySet()) {
            String K = entry.getKey();
            String[] Vs = entry.getValue();

            if (Vs.length != 0) {
                switch (K) {
                    case "round" -> {
                        try {
                            value = tools.roundNum(Float.parseFloat(value), Integer.parseInt(Vs[0]));
                        } catch (Exception ignored) {}
                    }
                    case "replace" -> {
                        try {
                            value = value.replaceAll(Vs[0], Vs[1]);
                        } catch (Exception ignored) {}
                    }
                    case "split" -> {
                        try {
                            value = value.split(Vs[0])[Integer.parseInt(Vs[1])];
                        } catch (Exception ignored) {}
                    }
                    case "substring" -> {
                        try {
                            value = Vs.length == 2 ? value.substring(Integer.parseInt(Vs[0]), Integer.parseInt(Vs[1])) : value.substring(Integer.parseInt(Vs[0]));
                        } catch (Exception ignored) {}
                    }
                    case "contains" -> {
                        try {
                            value = String.valueOf(value.contains(Vs[0]));
                        } catch (Exception ignored) {}
                    }
                    case "charat" -> {
                        try {
                            value = String.valueOf(value.charAt(Integer.parseInt(Vs[0])));
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
                    case "length" -> value = String.valueOf(value.length());
                }
            }
        }
        return value;
    }
}
