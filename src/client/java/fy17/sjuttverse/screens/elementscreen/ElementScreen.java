package fy17.sjuttverse.screens.elementscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ElementScreen extends Screen {
    private final Screen parent;
    private JsonObject elm;

    public ElementScreen(Screen parent, JsonElement elm) {
        super(Text.literal("Sjuttverse"));
        this.parent = parent;
        this.elm = elm.getAsJsonObject();
    }

    @Override
    protected void init() {
        TextFieldWidget name = new TextFieldWidget(textRenderer, (int) (width / 8 * 4.5), 90 - 10 + textRenderer.fontHeight / 2, width / 8 * 3, 20, Text.literal("Name"));
        name.setMaxLength(16);
        name.setText(elm.get("name").getAsString());
        addDrawableChild(name);
        TextFieldWidget value = new TextFieldWidget(textRenderer, (int) (width / 8 * 4.5), 120 - 10 + textRenderer.fontHeight / 2, width / 8 * 3, 20, Text.literal("Value"));
        value.setMaxLength(256);
        value.setText(elm.get("value").getAsString());
        addDrawableChild(value);
        TextFieldWidget red = new TextFieldWidget(textRenderer, (int) (width / 8 * 4.5), 150 - 10 + textRenderer.fontHeight / 2, ((width / 8 * 3) - 15) / 4, 20, Text.literal("Red"));
        red.setMaxLength(3);
        red.setText("255");
        red.setTooltip(Tooltip.of(Text.of("Red")));
        addDrawableChild(red);
        TextFieldWidget green = new TextFieldWidget(textRenderer, (int) (0.65625 * width + 1.25), 150 - 10 + textRenderer.fontHeight / 2, ((width / 8 * 3) - 15) / 4, 20, Text.literal("Green"));
        green.setMaxLength(3);
        green.setText("255");
        green.setTooltip(Tooltip.of(Text.of("Green")));
        addDrawableChild(green);
        TextFieldWidget blue = new TextFieldWidget(textRenderer, (int) (width * 0.75 + 2.5), 150 - 10 + textRenderer.fontHeight / 2, ((width / 8 * 3) - 15) / 4, 20, Text.literal("Blue"));
        blue.setMaxLength(3);
        blue.setText("255");
        blue.setTooltip(Tooltip.of(Text.of("Blue")));
        addDrawableChild(blue);
        TextFieldWidget alpha = new TextFieldWidget(textRenderer, (int) (0.84375 * width + 3.75), 150 - 10 + textRenderer.fontHeight / 2, ((width / 8 * 3) - 15) / 4, 20, Text.literal("Alpha"));
        alpha.setMaxLength(3);
        alpha.setText("1");
        alpha.setTooltip(Tooltip.of(Text.of("Alpha")));
        addDrawableChild(alpha);

        TextFieldWidget posX = new TextFieldWidget(textRenderer, (int) (width / 8 * 4.5), 180 - 10 + textRenderer.fontHeight / 2, width / 8 * 3, 20, Text.literal("Position X"));
        posX.setMaxLength(4);
        posX.setText(elm.get("posX").getAsString());
        addDrawableChild(posX);
        TextFieldWidget posY = new TextFieldWidget(textRenderer, (int) (width / 8 * 4.5), 210 - 10 + textRenderer.fontHeight / 2, width / 8 * 3, 20, Text.literal("Position Y"));
        posY.setMaxLength(4);
        posY.setText(elm.get("posY").getAsString());
        addDrawableChild(posY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Sjuttverse - Editing " + elm.get("name")), width / 2, 20, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("MAIN"), width / 2, 60, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Name:"), width / 4, 90, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Value:"), width / 4, 120, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Text Color (RGBA):"), width / 4, 150, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("X Position"), width / 4, 180, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Y Position"), width / 4, 210, 0xffffff);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (!(getFocused() instanceof TextFieldWidget) || !(getFocused().isMouseOver(mouseX, mouseY))) {
            setFocused(null);
        }
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