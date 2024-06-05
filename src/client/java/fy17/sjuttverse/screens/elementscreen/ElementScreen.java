package fy17.sjuttverse.screens.elementscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ElementScreen extends Screen {
    private final Screen parent;
    private ScrollableArea scrollableArea;
    private JsonObject elm;

    public ElementScreen(Screen parent, JsonElement elm) {
        super(Text.literal("Sjuttverse"));
        this.parent = parent;
        this.elm = elm.getAsJsonObject();
    }

    @Override
    protected void init() {
        scrollableArea = new ScrollableArea(height, width, this);
        addSelectableChild(scrollableArea);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        scrollableArea.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        setFocused(null);
        return true;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
