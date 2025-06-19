package avox.adaptivehud;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.DrawContext;

public abstract class HudElement {
    public String name;

    public HudElement(JsonObject savedData, Alignment alignment, String name) {
        this.name = name;
        this.alignment = alignment;
        this.alignment.element = this;
    }

    public Alignment alignment;

    public int x;
    public int y;
    public int relativeX;
    public int relativeY;

    public float scale = 1;

    public int getEstimatedWidth() {
        return (int) (getEstimatedWidthWithoutScale() * scale);
    }

    public int getEstimatedHeight() {
        return (int) (getEstimatedHeightWithoutScale() * scale);
    }

    // The estimated width of the element, the width it will have on the moving screen
    public abstract int getEstimatedWidthWithoutScale();
    public abstract int getEstimatedHeightWithoutScale();

    public abstract void render(DrawContext context);

    public abstract JsonObject elementToSave();
}