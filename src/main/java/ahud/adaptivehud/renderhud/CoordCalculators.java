package ahud.adaptivehud.renderhud;

import com.google.gson.JsonObject;

public class CoordCalculators {
    public int getActualCords(JsonObject elm, int value, int max, int length, float scale, String axis) {
        // Calculate actual cords from alignments
        int itemAlign = elm.get("alignment").getAsJsonObject().get("itemAlign" + axis).getAsInt();
        int selfAlign = elm.get("alignment").getAsJsonObject().get("selfAlign" + axis).getAsInt();
        int pos;
        if (itemAlign == 1) {
            pos = max / 2 + value;
        } else if (itemAlign == 2) {
            pos = max + value;
        } else {
            pos = value;
        }
        if (scale > 0) { // To allow calculating without scaling, set scale to 0
            pos = Math.round(pos / scale); // Scales coords to their normal size, first time I made an informative comment
        }
        if (selfAlign == 1) {
            pos -= length / 2;
        } else if (selfAlign == 2) {
            pos -= length;
        }
        return pos;
    }

    public int getRelativeCords(JsonObject elm, int left, int max, int length, String axis) {
        // Calculate relative cords to alignment
        int pos;
        int specWidth = 0;

        int itemAlign = elm.get("alignment").getAsJsonObject().get("itemAlign" + axis).getAsInt();
        int selfAlign = elm.get("alignment").getAsJsonObject().get("selfAlign" + axis).getAsInt();

        if (selfAlign == 1) {
            specWidth += length / 2;
        } else if (selfAlign == 2) {
            specWidth += length;
        }

        if (itemAlign == 1) {
            pos = (left + specWidth) - max / 2;
        } else if (itemAlign == 2) {
            pos = (left + specWidth) - max;
        } else {
            pos = left + specWidth;
        }

        return pos;
    }

    public int getTextAlignX(JsonObject elm, int totalWidth, int selfWidth, int padding) {
        int textAlign = elm.get("alignment").getAsJsonObject().get("textAlign").getAsInt();
        if (textAlign == 1) {
            return (totalWidth - selfWidth) / 2;
        } else if (textAlign == 2) {
            return totalWidth - selfWidth - padding;
        }
        return padding;
    }
}
