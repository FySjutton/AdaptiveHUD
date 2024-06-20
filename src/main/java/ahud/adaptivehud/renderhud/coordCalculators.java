package ahud.adaptivehud.renderhud;

import com.google.gson.JsonObject;
import static ahud.adaptivehud.adaptivehud.LOGGER;

public class coordCalculators {
    public int getActualCords(JsonObject elm, int value, int max, int length, float scale, String axis) {
        // Calculate actual cords from alignments
        int anchor = elm.get("alignment").getAsJsonObject().get("anchorPoint" + axis).getAsInt();
        int align = elm.get("alignment").getAsJsonObject().get("textAlign" + axis).getAsInt();
        int pos;
        if (anchor == 1) {
            pos = max / 2 + value;
        } else if (anchor == 2) {
            pos = max + value;
        } else {
            pos = value;
        }
        if (scale > 0) { // To allow calculating without scaling, set scale to 0
            pos = (int) (pos / scale); // Scales coords to their normal size, first time I made an informative comment
        }
        if (align == 1) {
            pos -= length / 2;
        } else if (align == 2) {
            pos -= length;
        }
        return pos;
    }

    public int getRelativeCords(JsonObject elm, int left, int max, int width, String axis) {
        // Calculate relative cords to alignment
        int pos;
        int specWidth = 0;

        int anchor = elm.get("alignment").getAsJsonObject().get("anchorPoint" + axis).getAsInt();
        int align = elm.get("alignment").getAsJsonObject().get("textAlign" + axis).getAsInt();

        if (align == 1) {
            specWidth += width / 2;
        } else if (align == 2) {
            specWidth += width;
        }

        if (anchor == 1) {
            pos = (left + specWidth) - max / 2;
        } else if (anchor == 2) {
            pos = (left + specWidth) - max;
        } else {
            pos = left + specWidth;
        }

        return pos;
    }
}
