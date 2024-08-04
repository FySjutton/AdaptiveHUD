package ahud.adaptivehud.screens.elementscreen;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollableArea extends ElementListWidget<ScrollableArea.Entry> {
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final ElementScreen PARENT;
    public final ArrayList<String> TITLES = new ArrayList<>(Arrays.asList("MAIN", "name", "value", "textColor", "posX", "posY", "shadow", "BACKGROUND", "enabled", "paddingX", "paddingY", "backgroundColor", "ALIGNMENT", "anchorPointX", "anchorPointY", "textAlignX", "textAlignY", "ADVANCED", "scale"));

    private static final String CENTER = Text.translatable("adaptivehud.config.button.center").getString();
    private static final String LEFT = Text.translatable("adaptivehud.config.button.left").getString();
    private static final String RIGHT = Text.translatable("adaptivehud.config.button.right").getString();
    private static final String TOP = Text.translatable("adaptivehud.config.button.top").getString();
    private static final String BOTTOM = Text.translatable("adaptivehud.config.button.bottom").getString();
    private static final String ON_TEXT = Text.translatable("adaptivehud.config.button.on").getString();
    private static final String OFF_TEXT = Text.translatable("adaptivehud.config.button.off").getString();

    public ScrollableArea(int height, int width, ElementScreen parent) {
        super(MinecraftClient.getInstance(), width, height - 100, 50, 25);
        this.PARENT = parent;

        for (String x : TITLES) {
            this.addEntry(new Entry(x));
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return width - 15;
    }

    @Override
    public int getRowWidth() {
        return width - 15;
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        public TextFieldWidget textField;
        public ButtonWidget button;
        public String setting;
        public String title;

        public Entry(String item) {
            this.setting = item;

            JsonObject parentElm;
            if (item.equals("enabled") || item.equals("paddingX") || item.equals("paddingY") || item.equals("backgroundColor")) {
                parentElm = PARENT.elm.get("background").getAsJsonObject();
            } else if (item.equals("anchorPointX") || item.equals("anchorPointY") || item.equals("textAlignX") || item.equals("textAlignY")) {
                parentElm = PARENT.elm.get("alignment").getAsJsonObject();
            } else if (item.equals("scale")) {
                parentElm = PARENT.elm.get("advanced").getAsJsonObject();
            } else {
                parentElm = PARENT.elm;
            }

            if (item.equals("MAIN") || item.equals("BACKGROUND") || item.equals("ALIGNMENT") || item.equals("ADVANCED")) {
                this.title = item;
            } else if (item.equals("shadow") || item.equals("enabled")) {
                this.button = ButtonWidget.builder(
                    Text.literal(parentElm.get(item).getAsBoolean() ? ON_TEXT : OFF_TEXT),
                    ScrollableArea.this::toggleOnOff
                )
                .dimensions(0, 0, 100, 20)
                .build();
            } else if (item.equals("anchorPointY") || item.equals("textAlignY")) {
                this.button = ButtonWidget.builder(
                    Text.literal(getY(parentElm.get(item).getAsInt())),
                    ScrollableArea.this::alignY
                )
                .dimensions(0, 0, 100, 20)
                .build();
            } else if (item.equals("anchorPointX") || item.equals("textAlignX")) {
                this.button = ButtonWidget.builder(
                    Text.literal(getX(parentElm.get(item).getAsInt())),
                    ScrollableArea.this::alignX
                )
                .dimensions(0, 0, 100, 20)
                .build();
            } else {
                this.textField = new TextFieldWidget(textRenderer, 0, 0, 100, 20, Text.literal(item));
                if (item.equals("value")) {
                    this.textField.setMaxLength(350);
                }
                if (item.equals("textColor") || item.equals("backgroundColor")) {
                    this.textField.setPlaceholder(Text.of("#RRGGBBAA"));
                }
                this.textField.setText(parentElm.get(item).getAsString());
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            List<Selectable> children = new ArrayList<>();
            if (button != null) {
                children.add(button);
            }
            if (textField != null) {
                children.add(textField);
            }
            return children;
        }

        @Override
        public List<? extends net.minecraft.client.gui.Element> children() {
            List<Element> children = new ArrayList<>();
            if (button != null) {
                children.add(button);
            }
            if (textField != null) {
                children.add(textField);
            }
            return children;
        }

        @Override
        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (this.textField != null) {
                this.textField.setX(width / 2 + 50);
                this.textField.setY(y);
                this.textField.render(drawContext, mouseX, mouseY, tickDelta);
                drawContext.drawText(textRenderer, Text.translatable("adaptivehud.config.setting." + TITLES.get(index)).getString(), width / 2 - 150, y + entryHeight / 2 - textRenderer.fontHeight / 2, 0xFFFFFF, true);
            }
            if (this.button != null) {
                this.button.setX(width / 2 + 50);
                this.button.setY(y);
                this.button.render(drawContext, mouseX, mouseY, tickDelta);
                drawContext.drawText(textRenderer, Text.translatable("adaptivehud.config.setting." + TITLES.get(index)).getString(), width / 2 - 150, y + entryHeight / 2 - textRenderer.fontHeight / 2, 0xFFFFFF, true);
            }
            if (this.title != null) {
                drawContext.drawCenteredTextWithShadow(textRenderer, Text.translatable("adaptivehud.config.title." + this.title).getString(), width / 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, 0xFFFFFF);
            }
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
            if (!(this.getFocused() instanceof TextFieldWidget)) {
                this.setFocused(null);
            } else if (!this.getFocused().isMouseOver(mouseX, mouseY)) {
                this.setFocused(null);
            }
            return true;
        }
    }

    private void toggleOnOff(ButtonWidget btn) {
        if (btn.getMessage().getString().equals(ON_TEXT)) {
            btn.setMessage(Text.of(OFF_TEXT));
        } else {
            btn.setMessage(Text.of(ON_TEXT));
        }
    }

    private void alignX(ButtonWidget btn) {
        if (btn.getMessage().getString().equals(LEFT)) {
            btn.setMessage(Text.of(CENTER));
        } else if (btn.getMessage().getString().equals(CENTER)) {
            btn.setMessage(Text.of(RIGHT));
        } else {
            btn.setMessage(Text.of(LEFT));
        }
    }

    private void alignY(ButtonWidget btn) {
        if (btn.getMessage().getString().equals(TOP)) {
            btn.setMessage(Text.of(CENTER));
        } else if (btn.getMessage().getString().equals(CENTER)) {
            btn.setMessage(Text.of(BOTTOM));
        } else {
            btn.setMessage(Text.of(TOP));
        }
    }

    private String getX(int number) {
        if (number == 0) {
            return LEFT;
        } else if (number == 1) {
            return CENTER;
        } else {
            return RIGHT;
        }
    }
    private String getY(int number) {
        if (number == 0) {
            return TOP;
        } else if (number == 1) {
            return CENTER;
        } else {
            return BOTTOM;
        }
    }
}