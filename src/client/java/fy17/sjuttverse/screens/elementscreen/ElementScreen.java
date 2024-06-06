package fy17.sjuttverse.screens.elementscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static fy17.sjuttverse.Sjuttverse.LOGGER;

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

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (scrollableArea.charTyped(chr, keyCode)) {
            return true;
        }
        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (scrollableArea.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (scrollableArea.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}
