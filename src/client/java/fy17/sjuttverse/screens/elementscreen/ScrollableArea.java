package fy17.sjuttverse.screens.elementscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static fy17.sjuttverse.Sjuttverse.LOGGER;


public class ScrollableArea extends ElementListWidget<ScrollableArea.Entry> {
    private final ElementScreen parent;
    private final ArrayList<String> titles = new ArrayList<>(Arrays.asList("name", "value", "textColor", "posX", "posY", "shadow"));
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public ScrollableArea(int height, int width, ElementScreen parent) {
        super(MinecraftClient.getInstance(), width, height - 100, 50, 25);
        this.parent = parent;

        for (String x : titles) {
            this.addEntry(new Entry(x));
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return width - 15;
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        private TextFieldWidget textField;
        private ButtonWidget button;

        public Entry(String item) {
            if (item.equals("shadow")) {
                this.button = ButtonWidget.builder(
                    Text.literal(parent.elm.get(item).getAsBoolean() ? "On" : "Off"),
                    ScrollableArea.this::toggleOnOff
                )
                    .dimensions(0, 0, 100, 20)
                    .build();
            } else {
                this.textField = new TextFieldWidget(textRenderer, 0, 0, 100, 20, Text.literal(item));
                if (item.equals("value")) {
                    this.textField.setMaxLength(350);
                }
                this.textField.setText(parent.elm.get(item).getAsString());
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return button != null ? List.of(button) : List.of(textField);
        }

        @Override
        public List<? extends net.minecraft.client.gui.Element> children() {
            return button != null ? List.of(button) : List.of(textField);
        }

        @Override
        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (this.textField != null) {
                this.textField.setX(width / 6 * 5 - 100);
                this.textField.setY(y);
                this.textField.render(drawContext, mouseX, mouseY, tickDelta);
            }
            if (this.button != null) {
                this.button.setX(width / 6 * 5 - 100);
                this.button.setY(y);
                this.button.render(drawContext, mouseX, mouseY, tickDelta);
            }

            drawContext.drawText(textRenderer, titles.get(index), width / 6, y + entryHeight / 2 - textRenderer.fontHeight / 2, 0xFFFFFF, true);
        }

        @Override
        public boolean charTyped(char chr, int keyCode) {
            return textField != null && textField.charTyped(chr, keyCode);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return textField != null && textField.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
//            if ((this.getFocused() instanceof TextFieldWidget && !this.getFocused().isMouseOver(mouseX, mouseY)) || this.getFocused() instanceof ButtonWidget) {
//                elm.add(titles.get(children().indexOf(this.getFocused())), this.getFocused().);
//                titles.get(children().indexOf(this.getFocused()));
//                parent.checkChanges();
//            }

            if (!(this.getFocused() instanceof TextFieldWidget)) {
                if (this.getFocused() instanceof ButtonWidget) {
                    LOGGER.info(String.valueOf(children().indexOf(this.getFocused())));
                    parent.elm.add(titles.get(children().indexOf(this.getFocused())), JsonParser.parseString(((ButtonWidget) this.getFocused()).getMessage().getString().equals("On") ? "true" : "false"));
                    LOGGER.info(String.valueOf(parent.elm));
                    this.setFocused(null);
                }
            } else if (!this.getFocused().isMouseOver(mouseX, mouseY)) {
                this.setFocused(null);
            }
            return true;
        }
    }

    private void toggleOnOff(ButtonWidget btn) {
        if (btn.getMessage().getString().equals("On")) {
            btn.setMessage(Text.of("Off"));
        } else {
            btn.setMessage(Text.of("On"));
        }
    }
}