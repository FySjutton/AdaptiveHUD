package fy17.sjuttverse.screens.elementscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import static fy17.sjuttverse.ConfigFiles.elementArray;
import static fy17.sjuttverse.Sjuttverse.LOGGER;

@Environment(EnvType.CLIENT)
public class ElementScreen extends Screen {
    private final Screen parent;
    private final JsonObject beforeEdit;
    private boolean fileChanged = false;
    private ScrollableArea scrollableArea;
    public JsonObject elm;

    public ElementScreen(Screen parent, JsonElement elm) {
        super(Text.literal("Sjuttverse"));
        this.parent = parent;
        this.elm = elm.getAsJsonObject();

        beforeEdit = elm.deepCopy().getAsJsonObject();
    }

    @Override
    protected void init() {
        ButtonWidget cancelBtn = ButtonWidget.builder(Text.literal("Cancel"), btn -> {close();})
            .dimensions(width / 2 - 105, height - 35, 100, 20)
            .build();
        addDrawableChild(cancelBtn);
        ButtonWidget saveBtn = ButtonWidget.builder(Text.literal("Done"), btn -> {})
                .dimensions(width / 2 + 5, height - 35, 100, 20)
                .build();
        addDrawableChild(saveBtn);

        scrollableArea = new ScrollableArea(height, width, this);
        addSelectableChild(scrollableArea);

        checkChanges();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
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
        return false;
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

    public void checkChanges() {
        LOGGER.info("HERE");
        if (fileChanged != elm.equals(beforeEdit)) {
            fileChanged = !elm.equals(beforeEdit);

            ButtonWidget cancelButton = (ButtonWidget) children().get(0);
            ButtonWidget saveButton = (ButtonWidget) children().get(1);

            cancelButton.active = fileChanged;
            saveButton.setMessage(Text.of(fileChanged ? "Save" : "Done"));
        }
    }
}
