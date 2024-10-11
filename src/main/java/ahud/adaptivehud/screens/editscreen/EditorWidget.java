package ahud.adaptivehud.screens.editscreen;

import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class EditorWidget extends ClickableWidget implements Drawable {
    private String text = "";
    private final TextRenderer textRenderer;
    private final int maxVisibleLines;
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted"));
    private int cursorPosition = 0;
    private int scroll = 0;

    private List<String> lines = new ArrayList<>();
    private List<String> displayLines = new ArrayList<>();
    private int selectionStart = 0;
    private int cursorY = 0;
    private int cursorX = 0;

    private long lastTime = 0;
    private long lastEditTime = 0;
    private boolean showCursor = true;

    public EditorWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text);
        this.textRenderer = textRenderer;
        maxVisibleLines = (height - 5) / 10;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier identifier = TEXTURES.get(this.isNarratable(), this.isFocused());
        context.drawGuiTexture(identifier, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        int y = getY() + 5;
        for (String line : displayLines) {
            if (y + 10 >= height) {
                break;
            }
            context.drawText(textRenderer, line, getX() + 5, y, 0xFFFFFFFF, true);
            y += 10;
        }

        if (showCursor && this.isFocused() && (scroll + maxVisibleLines > cursorY)) {
            context.fill(selectionStart + 5, (cursorY - scroll) * 10 + 3, selectionStart + 6, (cursorY - scroll) * 10 + 13, 0xFFFFFFFF);
        }
        if (Util.getMeasuringTimeMs() < lastEditTime + 800) {
            showCursor = true;
        } else if (Util.getMeasuringTimeMs() > lastTime + 530) {
            lastTime = Util.getMeasuringTimeMs();
            showCursor = !showCursor;
        }
    }

    private void updateDisplayLines() {
        if (maxVisibleLines < lines.size()) {
            displayLines = lines.subList(scroll, scroll + maxVisibleLines);
        } else {
            displayLines = lines;
        }
    }

    private void wrapLines() {
        List<String> wrappedLines = new ArrayList<>();

        TextHandler.LineWrappingConsumer consumer = (style, start, end) -> {
            String wrappedLine = text.substring(start, end);
            wrappedLines.add(wrappedLine);
        };

        textRenderer.getTextHandler().wrapLines(text, width - 10, Style.EMPTY, true, consumer);
        lines = wrappedLines;

        int length = 0;
        boolean found = false;

        for (int i = 0; i < wrappedLines.size(); i++) {
            int lineLength = wrappedLines.get(i).length();

            if (length <= cursorPosition && length + lineLength >= cursorPosition) {
                cursorX = cursorPosition - length;
                cursorY = i;
                found = true;
                break;
            }
            length += lineLength;
        }

        if (!found) {
            cursorY = Math.max(wrappedLines.size() - 1, 0);
            if (wrappedLines.isEmpty()) {
                cursorX = 0;
            }
        }

        if (!wrappedLines.isEmpty()) {
            String line = wrappedLines.get(cursorY);
            selectionStart = textRenderer.getWidth(line.substring(0, cursorX));
        }

        if (scroll + maxVisibleLines < cursorY + 1) {
            scroll = cursorY + 1 - maxVisibleLines;
        }

        lastEditTime = Util.getMeasuringTimeMs();
        updateDisplayLines();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.isActive()) {
            if (verticalAmount > 0 && scroll > 0) {
                scroll -= 1;
            } else if (verticalAmount < 0 && scroll + maxVisibleLines < lines.size()) {
                scroll += 1;
            }
            updateDisplayLines();
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isNarratable() && this.isFocused()) {
            switch (keyCode) {
                case 259: // Backspace
                    this.erase(1);
                    break;
                case 263: // Left Arrow
                    if (cursorPosition > 0) {
                        cursorPosition--;
                        wrapLines();
                    }
                    break;
                case 262: // Right Arrow
                    if (cursorPosition < text.length()) {
                        cursorPosition++;
                        wrapLines();
                    }
                    break;
                case 265: // Up Arrow
                    if (cursorY > 0) {
                        cursorY--;
                        cursorX = Math.min(cursorX, lines.get(cursorY).length());

                        cursorPosition = getCursorFromLineAndIndex(cursorY, cursorX);
                        updateDisplayLines();
                    }
                    break;
                case 264: // Down Arrow
                    if (cursorY < lines.size() - 1) {
                        cursorY++;
                        cursorX = Math.min(cursorX, lines.get(cursorY).length());

                        cursorPosition = getCursorFromLineAndIndex(cursorY, cursorX);
                        updateDisplayLines();
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isActive()) {
            if (mouseX >= this.getX() && mouseX <= this.getX() + this.width && mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
                int clickedLine = ((int) (mouseY - this.getY() - 5) / 10) + scroll;
                clickedLine = Math.max(0, clickedLine);
                clickedLine = Math.min(lines.size() - 1, clickedLine);

                String line = lines.get(clickedLine);
                int clickX = (int) (mouseX - this.getX() - 5);
                int charIndex = textRenderer.trimToWidth(line, clickX).length();

                this.cursorY = clickedLine;
                this.cursorX = charIndex;
                this.cursorPosition = getCursorFromLineAndIndex(clickedLine, charIndex);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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

    private int getCursorFromLineAndIndex(int lineNumber, int charIndex) {
        int cursor = 0;
        for (int i = 0; i < lineNumber; i++) {
            cursor += lines.get(i).length();
        }
        selectionStart = textRenderer.getWidth(lines.get(lineNumber).substring(0, charIndex));
        return cursor + charIndex;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
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

    public boolean isActive() {
        return this.isNarratable() && this.isFocused();
    }
}
