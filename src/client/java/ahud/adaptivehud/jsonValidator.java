package ahud.adaptivehud;

import com.google.gson.JsonObject;

public class jsonValidator {
    public String validateElement(JsonObject elm) {
        try {
            JsonObject background = elm.get("background").getAsJsonObject();
            elm.get("enabled").getAsBoolean();
            elm.get("name").getAsString();
            elm.get("textColor").getAsString();
            elm.get("posX").getAsInt();
            elm.get("posY").getAsInt();
            elm.get("shadow").getAsBoolean();
            background.get("enabled").getAsBoolean();
            background.get("paddingX").getAsInt();
            background.get("paddingY").getAsInt();
            background.get("backgroundColor").getAsString();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String validateConfig(JsonObject elm) {
        try {
            int default_size = elm.get("default_size").getAsInt();
            if (default_size > 0.1 && default_size < 10) {
                return null;
            } else {
                return "Default size must be \"0.1 > default size < 10\".";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
