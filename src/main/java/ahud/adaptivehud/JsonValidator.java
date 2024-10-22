package ahud.adaptivehud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ahud.adaptivehud.ConfigFiles.elementArray;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

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
        addDefaultProperty(alignment, "itemAlignX", new JsonPrimitive(0));
        addDefaultProperty(alignment, "itemAlignY", new JsonPrimitive(0));
        addDefaultProperty(alignment, "selfAlignX", new JsonPrimitive(0));
        addDefaultProperty(alignment, "selfAlignY", new JsonPrimitive(0));
        addDefaultProperty(alignment, "textAlign", new JsonPrimitive(0));

        JsonObject advanced = getOrCreateObject(obj, "advanced");
        addDefaultProperty(advanced, "scale", new JsonPrimitive(0));
        addDefaultProperty(advanced, "renderRequirement", new JsonPrimitive(""));

        return elm;
    }

    public String validateColor(String check) {
        if (!COLOR_REGEX.matcher(check).matches()) {
            return Text.translatable("adaptivehud.config.error.invalid_color").getString();
        }
        return null;
    }

    private String validateAlignment(JsonObject alignment) {
        if (!ALLOWED_POS.contains(alignment.get("itemAlignX").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("itemAlignY").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("selfAlignX").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("selfAlignY").getAsInt()) ||
                !ALLOWED_POS.contains(alignment.get("textAlign").getAsInt())
        ){
            return Text.translatable("adaptivehud.config.error.invalid_alignment").getString();
        }
        return null;
    }

    public String validateScale(String value) {
        try {
            float scale = Float.parseFloat(value);
            if (scale < 0 || scale >= 10) {
                return Text.translatable("adaptivehud.config.error.invalid_scale").getString();
            }
            return null;
        } catch (Exception e) {
            return Text.translatable("adaptivehud.config.error.invalid_scale").getString();
        }
    }

    public String validateName(JsonObject beforeEdit, String name, boolean newFile) {
        if (!name.matches("[a-z_\\d]{1,16}")) {
            return Text.translatable("adaptivehud.config.error.invalid_name").getString();
        }

        if (newFile) return null;

        List<JsonElement> deepCopyArray = elementArray.stream()
                .map(JsonElement::deepCopy)
                .collect(Collectors.toList());

        deepCopyArray.remove(beforeEdit);

        boolean hasSameName = deepCopyArray.stream().anyMatch(elm -> elm.getAsJsonObject().get("name").getAsString().equals(name));
        return hasSameName ? Text.translatable("adaptivehud.config.error.invalid_name").getString() : null;
    }

    public String validateElement(JsonObject elm, boolean newFile) {
        try {
            JsonObject background = elm.getAsJsonObject("background");
            JsonObject alignment = elm.getAsJsonObject("alignment");
            JsonObject advanced = elm.getAsJsonObject("advanced");

            elm.get("enabled").getAsBoolean();
            String name = validateName(elm, elm.get("name").getAsString(), newFile);
            if (name != null) return name;
            elm.get("posX").getAsInt();
            elm.get("posY").getAsInt();
            elm.get("shadow").getAsBoolean();
            background.get("enabled").getAsBoolean();
            background.get("paddingX").getAsInt();
            background.get("paddingY").getAsInt();
            advanced.get("renderRequirement").getAsString();

            String textColor = validateColor(elm.get("textColor").getAsString());
            if (textColor != null) return textColor;
            String backgroundColor = validateColor(background.get("backgroundColor").getAsString());
            if (backgroundColor != null) return backgroundColor;

            String alignmentValidation = validateAlignment(alignment);
            if (alignmentValidation != null) return alignmentValidation;

            // Same as the one above, since it's the last one it'll return null if no error at all
            return validateScale(advanced.get("scale").getAsString());

        } catch (Exception e) {
            return "Validation error: " + e.getMessage();
        }
    }

    public String validateConfig(JsonObject elm) {
        try {
            double default_size = elm.get("default_size").getAsDouble();
            if (!(default_size > 0.1 && default_size < 10)) {
                return "Default size must be \"0.1 > default size < 10\".";
            }

            elm.get("render_on_debug").getAsBoolean();
            elm.get("render_on_f1").getAsBoolean();
            elm.get("render_get_help_button").getAsBoolean();
            elm.get("max_target_block_distance").getAsDouble();
            elm.get("variable_reload_cooldown").getAsInt();

            if (
                new Tools().parseColor(elm.get("snapping_lines_color").getAsString()) == 0 ||
                new Tools().parseColor(elm.get("item_align_lines_color").getAsString()) == 0
            ) {
                return "Invalid color in config file!";
            }

            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
