package fy17.sjuttverse.screens.elementscreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fy17.sjuttverse.Sjuttverse.LOGGER;


public class ScrollableArea extends ElementListWidget<ScrollableArea.Entry> {
    private final ElementScreen parent;
    private final ArrayList<String> titles = new ArrayList<>(Arrays.asList("Hejsan", "b"));
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public ScrollableArea(int height, int width, ElementScreen parent) {
        super(MinecraftClient.getInstance(), width, height - 100, 50, 25);
        this.parent = parent;

        for (int i = 0; i < titles.size(); i++) {
            this.addEntry(new Entry(i));
        }
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        private final TextFieldWidget textField;

        public Entry(int i) {
            this.textField = new TextFieldWidget(textRenderer, 100, 20 + i * 5, Text.literal("waha" + i));
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(textField);
        }

        @Override
        public List<? extends net.minecraft.client.gui.Element> children() {
            return List.of(textField);
        }

        @Override
        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.textField.setX(x + width / 2 - entryWidth / 4);
            this.textField.setY(y);
            this.textField.render(drawContext, mouseX, mouseY, tickDelta);


            drawContext.drawText(textRenderer, titles.get(index), 25, y + entryHeight / 2 - textRenderer.fontHeight / 2, 0xFFFFFF, true);
        }

        @Override
        public boolean charTyped(char chr, int keyCode) {
            return textField.charTyped(chr, keyCode);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return textField.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}