package ahud.adaptivehud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class jsonValidator {
    public JsonElement repairElement(JsonElement elm) {
        JsonObject obj = elm.getAsJsonObject();
        if (!obj.has("enabled")) {obj.addProperty("enabled", true);}
        if (!obj.has("textColor")) {obj.addProperty("textColor", "#FFFFFF");}
        if (!obj.has("posX")) {obj.addProperty("posX", 0);}
        if (!obj.has("posY")) {obj.addProperty("posY", 0);}
        if (!obj.has("shadow")) {obj.addProperty("shadow", true);}

        if (!obj.has("background")) {obj.add("background", new JsonObject());}
        JsonObject background = obj.get("background").getAsJsonObject();
        if (!background.has("enabled")) {background.addProperty("enabled", false);}
        if (!background.has("paddingX")) {background.addProperty("paddingX", 5);}
        if (!background.has("paddingY")) {background.addProperty("paddingY", 5);}
        if (!background.has("backgroundColor")) {background.addProperty("backgroundColor", "#0000004c");}

        if (!obj.has("alignment")) {obj.add("alignment", new JsonObject());}
        JsonObject alignment = obj.get("alignment").getAsJsonObject();
        if (!alignment.has("anchorPointX")) {alignment.addProperty("anchorPointX", 0);}
        if (!alignment.has("anchorPointY")) {alignment.addProperty("anchorPointY", 0);}
        if (!alignment.has("textAlignX")) {alignment.addProperty("textAlignX", 0);}
        if (!alignment.has("textAlignY")) {alignment.addProperty("textAlignY", 0);}

        if (!obj.has("advanced")) {obj.add("advanced", new JsonObject());}
        JsonObject advanced = obj.get("advanced").getAsJsonObject();
        if (!advanced.has("scale")) {advanced.addProperty("scale", 0);}

        return elm;
    }

    public String validateElement(JsonObject elm) {
        Pattern colorReg = Pattern.compile("^#?([0-9A-Fa-f]{6})([0-9A-Fa-f]{2})?$");
        ArrayList<Integer> allowedPos = new ArrayList<>(Arrays.asList(0, 1, 2));
//        0: LEFT, TOP
//        1: CENTER
//        2: RIGHT, BOTTOM
        try {
            JsonObject background = elm.get("background").getAsJsonObject();
            JsonObject alignment = elm.get("alignment").getAsJsonObject();
            JsonObject advanced = elm.get("advanced").getAsJsonObject();

            elm.get("enabled").getAsBoolean();
            elm.get("name").getAsString();
            elm.get("posX").getAsInt();
            elm.get("posY").getAsInt();
            elm.get("shadow").getAsBoolean();
            background.get("enabled").getAsBoolean();
            background.get("paddingX").getAsInt();
            background.get("paddingY").getAsInt();

            if (
                !(colorReg.matcher(elm.get("textColor").getAsString()).find() &&
                colorReg.matcher(background.get("backgroundColor").getAsString()).find())
            ) {
                return "Invalid color!";
            }
            if (
                !(allowedPos.contains(alignment.get("anchorPointX").getAsInt()) &&
                allowedPos.contains(alignment.get("anchorPointY").getAsInt()) &&
                allowedPos.contains(alignment.get("textAlignX").getAsInt()) &&
                allowedPos.contains(alignment.get("textAlignY").getAsInt()))
            ) {
                return "Invalid alignment!";
            }
            if (
                !(advanced.get("scale").getAsFloat() >= 0 &&
                advanced.get("scale").getAsFloat() < 10)
            ) {
                return "Invalid scale!";
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String validateConfig(JsonObject elm) {
        try {
            double default_size = elm.get("default_size").getAsDouble();
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
