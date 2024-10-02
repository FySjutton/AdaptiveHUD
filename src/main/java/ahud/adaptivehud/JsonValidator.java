package ahud.adaptivehud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

public class JsonValidator {
    private static final Pattern COLOR_REGEX = Pattern.compile("^#?([0-9A-Fa-f]{6})([0-9A-Fa-f]{2})?$");
    private static final Set<Integer> ALLOWED_POS = Set.of(0, 1, 2);

    private void addDefaultProperty(JsonObject obj, String key, JsonElement defaultValue) {
        if (!obj.has(key)) {
            obj.add(key, defaultValue);
        }
    }

    private JsonObject getOrCreateObject(JsonObject parent, String key) {
        if (!parent.has(key)) {
            parent.add(key, new JsonObject());
        }
        return parent.getAsJsonObject(key);
    }

    public JsonElement repairElement(JsonElement elm) {
        JsonObject obj = elm.getAsJsonObject();

        addDefaultProperty(obj, "enabled", new JsonPrimitive(true));
        addDefaultProperty(obj, "textColor", new JsonPrimitive("#FFFFFF"));
        addDefaultProperty(obj, "posX", new JsonPrimitive(0));
        addDefaultProperty(obj, "posY", new JsonPrimitive(0));
        addDefaultProperty(obj, "shadow", new JsonPrimitive(true));

        JsonObject background = getOrCreateObject(obj, "background");
        addDefaultProperty(background, "enabled", new JsonPrimitive(false));
        addDefaultProperty(background, "paddingX", new JsonPrimitive(5));
        addDefaultProperty(background, "paddingY", new JsonPrimitive(5));
        addDefaultProperty(background, "backgroundColor", new JsonPrimitive("#0000004c"));

        JsonObject alignment = getOrCreateObject(obj, "alignment");
        addDefaultProperty(alignment, "anchorPointX", new JsonPrimitive(0));
        addDefaultProperty(alignment, "anchorPointY", new JsonPrimitive(0));
        addDefaultProperty(alignment, "textAlignX", new JsonPrimitive(0));
        addDefaultProperty(alignment, "textAlignY", new JsonPrimitive(0));

        JsonObject requirement = getOrCreateObject(obj, "requirement");
        addDefaultProperty(requirement, "renderRequirement", new JsonPrimitive(""));

        JsonObject advanced = getOrCreateObject(obj, "advanced");
        addDefaultProperty(advanced, "scale", new JsonPrimitive(0));

        return elm;
    }

    private String validateColor(JsonObject elm, JsonObject background) {
        if (!COLOR_REGEX.matcher(elm.get("textColor").getAsString()).find() ||
                !COLOR_REGEX.matcher(background.get("backgroundColor").getAsString()).find()) {
            return "Invalid color!";
        }
        return null;
    }

    private String validateAlignment(JsonObject alignment) {
        if (!ALLOWED_POS.contains(alignment.get("anchorPointX").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("anchorPointY").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("textAlignX").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("textAlignY").getAsInt())) {
            return "Invalid alignment!";
        }
        return null;
    }

    private String validateScale(JsonObject advanced) {
        float scale = advanced.get("scale").getAsFloat();
        if (scale < 0 || scale >= 10) {
            return "Invalid scale!";
        }
        return null;
    }

    public String validateElement(JsonObject elm) {
        try {
            JsonObject background = elm.getAsJsonObject("background");
            JsonObject alignment = elm.getAsJsonObject("alignment");
            JsonObject advanced = elm.getAsJsonObject("advanced");
            JsonObject requirement = elm.getAsJsonObject("requirement");

            elm.get("enabled").getAsBoolean();
            elm.get("name").getAsString();
            elm.get("posX").getAsInt();
            elm.get("posY").getAsInt();
            elm.get("shadow").getAsBoolean();
            background.get("enabled").getAsBoolean();
            background.get("paddingX").getAsInt();
            background.get("paddingY").getAsInt();
            requirement.get("renderRequirement").getAsString();

            String colorValidation = validateColor(elm, background);
            if (colorValidation != null) return colorValidation;

            String alignmentValidation = validateAlignment(alignment);
            if (alignmentValidation != null) return alignmentValidation;

            // Same as the one above, since it's the last one it'll return null if no error at all
            return validateScale(advanced);

        } catch (Exception e) {
            return "Validation error: " + e.getMessage();
        }
    }





//    public JsonElement repairElement(JsonElement elm) {
//        JsonObject obj = elm.getAsJsonObject();
//        if (!obj.has("enabled")) {obj.addProperty("enabled", true);}
//        if (!obj.has("textColor")) {obj.addProperty("textColor", "#FFFFFF");}
//        if (!obj.has("posX")) {obj.addProperty("posX", 0);}
//        if (!obj.has("posY")) {obj.addProperty("posY", 0);}
//        if (!obj.has("shadow")) {obj.addProperty("shadow", true);}
//
//        if (!obj.has("background")) {obj.add("background", new JsonObject());}
//        JsonObject background = obj.get("background").getAsJsonObject();
//        if (!background.has("enabled")) {background.addProperty("enabled", false);}
//        if (!background.has("paddingX")) {background.addProperty("paddingX", 5);}
//        if (!background.has("paddingY")) {background.addProperty("paddingY", 5);}
//        if (!background.has("backgroundColor")) {background.addProperty("backgroundColor", "#0000004c");}
//
//        if (!obj.has("alignment")) {obj.add("alignment", new JsonObject());}
//        JsonObject alignment = obj.get("alignment").getAsJsonObject();
//        if (!alignment.has("anchorPointX")) {alignment.addProperty("anchorPointX", 0);}
//        if (!alignment.has("anchorPointY")) {alignment.addProperty("anchorPointY", 0);}
//        if (!alignment.has("textAlignX")) {alignment.addProperty("textAlignX", 0);}
//        if (!alignment.has("textAlignY")) {alignment.addProperty("textAlignY", 0);}
//
//        if (!obj.has("requirement")) {obj.add("requirement", new JsonObject());}
//        JsonObject requirement = obj.get("requirement").getAsJsonObject();
//        if (!requirement.has("renderRequirement")) {requirement.addProperty("renderRequirement", "");}
//
//        if (!obj.has("advanced")) {obj.add("advanced", new JsonObject());}
//        JsonObject advanced = obj.get("advanced").getAsJsonObject();
//        if (!advanced.has("scale")) {advanced.addProperty("scale", 0);}
//
//        return elm;
//    }
//
//    public String validateElement(JsonObject elm) {
//        Pattern colorReg = Pattern.compile("^#?([0-9A-Fa-f]{6})([0-9A-Fa-f]{2})?$");
//        ArrayList<Integer> allowedPos = new ArrayList<>(Arrays.asList(0, 1, 2));
////        0: LEFT, TOP
////        1: CENTER
////        2: RIGHT, BOTTOM
//        try {
//            JsonObject background = elm.get("background").getAsJsonObject();
//            JsonObject alignment = elm.get("alignment").getAsJsonObject();
//            JsonObject advanced = elm.get("advanced").getAsJsonObject();
//            JsonObject requirement = elm.get("requirement").getAsJsonObject();
//
//            elm.get("enabled").getAsBoolean();
//            elm.get("name").getAsString();
//            elm.get("posX").getAsInt();
//            elm.get("posY").getAsInt();
//            elm.get("shadow").getAsBoolean();
//            background.get("enabled").getAsBoolean();
//            background.get("paddingX").getAsInt();
//            background.get("paddingY").getAsInt();
//            requirement.get("renderRequirement").getAsString();
//
//            if (
//                !(colorReg.matcher(elm.get("textColor").getAsString()).find() &&
//                colorReg.matcher(background.get("backgroundColor").getAsString()).find())
//            ) {
//                return "Invalid color!";
//            }
//            if (
//                !(allowedPos.contains(alignment.get("anchorPointX").getAsInt()) &&
//                allowedPos.contains(alignment.get("anchorPointY").getAsInt()) &&
//                allowedPos.contains(alignment.get("textAlignX").getAsInt()) &&
//                allowedPos.contains(alignment.get("textAlignY").getAsInt()))
//            ) {
//                return "Invalid alignment!";
//            }
//            if (
//                !(advanced.get("scale").getAsFloat() >= 0 &&
//                advanced.get("scale").getAsFloat() < 10)
//            ) {
//                return "Invalid scale!";
//            }
//            return null;
//        } catch (Exception e) {
//            return e.getMessage();
//        }
//    }

    public String validateConfig(JsonObject elm) {
        try {
            double default_size = elm.get("default_size").getAsDouble();
            if (!(default_size > 0.1 && default_size < 10)) {
                return "Default size must be \"0.1 > default size < 10\".";
            }

            elm.get("render_on_debug").getAsBoolean();
            elm.get("render_get_help_button").getAsBoolean();
            elm.get("max_target_block_distance").getAsDouble();
            elm.get("variable_reload_cooldown").getAsInt();

            if (
                new Tools().parseColor(elm.get("snapping_lines_color").getAsString()) == 0 ||
                new Tools().parseColor(elm.get("anchor_point_lines_color").getAsString()) == 0
            ) {
                return "Invalid color in config file!";
            }

            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
