package ahud.adaptivehud.renderhud.variables;

import org.apache.commons.lang3.text.WordUtils;

public class FlagParser {
    public String parseFlags(String value, String flagString) {
        for (String x : flagString.split(" *-")) {
            switch (x) {
                case "uc" -> value = value.toUpperCase();
                case "lc" -> value = value.toLowerCase();
                case "tc" -> value = WordUtils.capitalizeFully(value);
                case "nd" -> value = value.replaceAll("[-_]", " ");
                case "sh" -> value = value.split(":")[1];
            }
        }
        return value;
    }
}
