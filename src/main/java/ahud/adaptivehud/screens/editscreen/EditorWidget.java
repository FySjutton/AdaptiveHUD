package ahud.adaptivehud.screens.editscreen;

import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

import ahud.adaptivehud.Tools;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

@Environment(EnvType.CLIENT)
public class EditorWidget extends ScrollableWidget {
    private final TextRenderer textRenderer;
    private final EditBox editBox;
    private long lastSwitchFocusTime = Util.getMeasuringTimeMs();
    private final Tools tools = new Tools();

    public EditorWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.editBox = new EditBox(textRenderer, width - this.getPaddingDoubled());
        this.editBox.setCursorChangeListener(this::onCursorChange);
    }

    public void setMaxLength(int maxLength) {
        this.editBox.setMaxLength(maxLength);
    }

    public void setChangeListener(Consumer<String> changeListener) {
        this.editBox.setChangeListener(changeListener);
    }

    public void setText(String text) {
        this.editBox.setText(text);
    }

    public String getText() {
        return this.editBox.getText();
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, Text.translatable("gui.narrate.editBox", new Object[]{this.getMessage(), this.getText()}));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isWithinBounds(mouseX, mouseY) && button == 0) {
            this.editBox.setSelecting(Screen.hasShiftDown());
            this.moveCursor(mouseX, mouseY);
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        } else if (this.isWithinBounds(mouseX, mouseY) && button == 0) {
            this.editBox.setSelecting(true);
            this.moveCursor(mouseX, mouseY);
            this.editBox.setSelecting(Screen.hasShiftDown());
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.editBox.handleSpecialKey(keyCode);
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.visible && this.isFocused() && StringHelper.isValidChar(chr)) {
            this.editBox.replaceSelection(Character.toString(chr));
            return true;
        } else {
            return false;
        }
    }

    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        String string = this.editBox.getText();
        if (!(string.isEmpty() && !this.isFocused())) {
            int i = this.editBox.getCursor();
            boolean bl = this.isFocused() && (Util.getMeasuringTimeMs() - this.lastSwitchFocusTime) / 300L % 2L == 0L;
            boolean bl2 = i < string.length();
            int j = 0;
            int k = 0;
            int l = this.getY() + this.getPadding();

            int var10002;
            int var10004;
            Stack<Character> openings = new Stack<>();
            openings.add('0');

            for(Iterator<EditBox.Substring> var12 = this.editBox.getLines().iterator(); var12.hasNext(); l += 9) {
                EditBox.Substring substring = var12.next();
                Objects.requireNonNull(this.textRenderer);
                boolean bl3 = this.isVisible(l, l + 9);
                if (bl && bl2 && i >= substring.beginIndex() && i <= substring.endIndex()) {
                    if (bl3) {
                        MutableText part1 = tools.colorTextRenderer(string.substring(substring.beginIndex(), substring.endIndex()), i - substring.beginIndex(), openings);
                        j = context.drawTextWithShadow(textRenderer, part1, this.getX() + this.getPadding(), l, 0xFFFFFFFF) - 1;
                        var10002 = l - 1;
                        int var10003 = j + 1;
                        var10004 = l + 1;
                        Objects.requireNonNull(this.textRenderer);
                        int oldJ = j;
                        MutableText part2 = tools.colorTextRenderer(string.substring(i, substring.endIndex()), substring.endIndex() - i, openings);
                        j = context.drawTextWithShadow(textRenderer, part2, j, l, 0xFFFFFFFF);
                        context.fill(oldJ, var10002, var10003, var10004 + 9, 0xFFFFFFFF);
                    }
                } else {
                    if (bl3) {
                        MutableText displayText = tools.colorTextRenderer(string.substring(substring.beginIndex(), substring.endIndex()), substring.endIndex() - substring.beginIndex(), openings);
                        j = context.drawTextWithShadow(textRenderer, displayText, this.getX() + this.getPadding(), l, 0xFFFFFFFF);
                    }

                    k = l;
                }

                Objects.requireNonNull(this.textRenderer);
            }

            if (bl && !bl2) {
                Objects.requireNonNull(this.textRenderer);
                if (this.isVisible(k, k + 9)) {
                    context.drawTextWithShadow(this.textRenderer, "_", j, k, -3092272);
                }
            }

            if (this.editBox.hasSelection()) {
                EditBox.Substring substring2 = this.editBox.getSelection();
                int m = this.getX() + this.getPadding();
                l = this.getY() + this.getPadding();

                for (EditBox.Substring substring3 : this.editBox.getLines()) {
                    if (substring2.beginIndex() > substring3.endIndex()) {
                        Objects.requireNonNull(this.textRenderer);
                        l += 9;
                    } else {
                        if (substring3.beginIndex() > substring2.endIndex()) {
                            break;
                        }

                        Objects.requireNonNull(this.textRenderer);
                        if (this.isVisible(l, l + 9)) {
                            int n = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                            int o;
                            if (substring2.endIndex() > substring3.endIndex()) {
                                o = this.width - this.getPadding();
                            } else {
                                o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                            }

                            var10002 = m + n;
                            var10004 = m + o;
                            Objects.requireNonNull(this.textRenderer);
                            this.drawSelection(context, var10002, l, var10004, l + 9);
                        }

                        Objects.requireNonNull(this.textRenderer);
                        l += 9;
                    }
                }
            }

        }
    }

    protected void renderOverlay(DrawContext context) {
        super.renderOverlay(context);
        if (this.editBox.hasMaxLength()) {
            int i = this.editBox.getMaxLength();
            Text text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
            context.drawTextWithShadow(this.textRenderer, text, this.getX() + this.width - this.textRenderer.getWidth(text), this.getY() + this.height + 4, 10526880);
        }

    }

    public int getContentsHeight() {
        Objects.requireNonNull(this.textRenderer);
        return 9 * this.editBox.getLineCount();
    }

    protected boolean overflows() {
        return (double)this.editBox.getLineCount() > this.getMaxLinesWithoutOverflow();
    }

    protected double getDeltaYPerScroll() {
        Objects.requireNonNull(this.textRenderer);
        return 9.0 / 2.0;
    }

    private void drawSelection(DrawContext context, int left, int top, int right, int bottom) {
        context.fill(RenderLayer.getGuiTextHighlight(), left, top, right, bottom, -16776961);
    }

    private void onCursorChange() {
        double d = this.getScrollY();
        EditBox var10000 = this.editBox;
        Objects.requireNonNull(this.textRenderer);
        EditBox.Substring substring = var10000.getLine((int)(d / 9.0));
        int var5;
        if (this.editBox.getCursor() <= substring.beginIndex()) {
            var5 = this.editBox.getCurrentLineIndex();
            Objects.requireNonNull(this.textRenderer);
            d = var5 * 9;
        } else {
            double var10001 = d + (double)this.height;
            Objects.requireNonNull(this.textRenderer);
            EditBox.Substring substring2 = var10000.getLine((int)(var10001 / 9.0) - 1);
            if (this.editBox.getCursor() > substring2.endIndex()) {
                var5 = this.editBox.getCurrentLineIndex();
                Objects.requireNonNull(this.textRenderer);
                var5 = var5 * 9 - this.height;
                Objects.requireNonNull(this.textRenderer);
                d = var5 + 9 + this.getPaddingDoubled();
            }
        }

        this.setScrollY(d);
    }

    private double getMaxLinesWithoutOverflow() {
        double var10000 = this.height - this.getPaddingDoubled();
        Objects.requireNonNull(this.textRenderer);
        return var10000 / 9.0;
    }

    private void moveCursor(double mouseX, double mouseY) {
        double d = mouseX - (double)this.getX() - (double)this.getPadding();
        double e = mouseY - (double)this.getY() - (double)this.getPadding() + this.getScrollY();
        this.editBox.moveCursor(d, e);
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.lastSwitchFocusTime = Util.getMeasuringTimeMs();
        }
    }
}
