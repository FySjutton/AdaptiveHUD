package ahud.adaptivehud.screens.editscreen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class EditorWidget extends ClickableWidget implements Drawable {
    private String text = "";
    private final TextRenderer textRenderer;
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted"));
    private int cursorPosition = 0;
    private int scroll = 0;
    private int maxVisibleLines;

    private List<OrderedText> lines = new ArrayList<>();
    private int cursorOnLine = 0;

    public EditorWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text);
        this.textRenderer = textRenderer;
        maxVisibleLines = (height - 5) / 10;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier identifier = TEXTURES.get(this.isNarratable(), this.isFocused());
        context.drawGuiTexture(identifier, this.getX(), this.getY(), this.getWidth(), this.getHeight());

//        List<OrderedText> lines = textRenderer.wrapLines(StringVisitable.plain(text), width - 10);
        List<OrderedText> cutList = lines;
        if (maxVisibleLines < lines.size()) {
            if (scroll > lines.size() - maxVisibleLines) {
                scroll --;
            }
            cutList = lines.subList(lines.size() - scroll < maxVisibleLines ? lines.size() - maxVisibleLines : scroll, Math.min(scroll + maxVisibleLines, lines.size()));
        }
        int y = getY() + 5;
        for (OrderedText line : cutList) {
            if (y + 10 >= height) {
                break;
            }
            context.drawText(textRenderer, line, getX() + 5, y, 0xFFFFFFFF, true);
            y += 10;
        }
    }

    private void wrapLines() {
        lines = textRenderer.wrapLines(StringVisitable.plain(text), width - 10);
        int length = 0;
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            StringBuilder builder = new StringBuilder();

            lines.get(i).accept((index, style, codePoint) -> {
                builder.append(Character.toChars(codePoint));
                return true;
            });

            int thisLength = builder.toString().length();
            if (cursorPosition < length) {
                cursorOnLine = i + 1;
                found = true;
                break;
            } else {
                length += thisLength;
            }
        }
        if (!found) {
            cursorOnLine = lines.size();
        }
        if (scroll + maxVisibleLines < cursorOnLine) {
            scroll = cursorOnLine - maxVisibleLines;
        }
    }

    public boolean isActive() {
        return this.isNarratable() && this.isFocused();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isNarratable() && this.isFocused()) {
            switch (keyCode) {
                case 259: this.erase(1);
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.isActive()) {
            if (verticalAmount > 0 && scroll > 0) {
                scroll -= 1;
            } else if (verticalAmount < 0) {
                scroll += 1;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isActive()) {
            return false;
        } else if (StringHelper.isValidChar(chr)) {
            write(Character.toString(chr));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    private void write(String writeText) {
        String start = text.substring(0, cursorPosition);
        String end = text.substring(cursorPosition);
        text = start + writeText + end;
        cursorPosition += writeText.length();
        wrapLines();
    }

    private void erase(int characters) {
        String start = text.substring(0, Math.max(cursorPosition - characters, 0));
        String end = text.substring(cursorPosition);
        cursorPosition = Math.max(cursorPosition - characters, 0);
        text = start + end;
        wrapLines();
    }
}
