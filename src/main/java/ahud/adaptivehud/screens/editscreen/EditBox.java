package ahud.adaptivehud.screens.editscreen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

@Environment(EnvType.CLIENT)
public class EditBox {
    private final TextRenderer textRenderer;
    private final List<Substring> lines = Lists.newArrayList();
    private String text;
    private int cursor;
    private int selectionEnd;
    private boolean selecting;
    private final int width;
    private Consumer<String> changeListener = (text) -> {};
    private Runnable cursorChangeListener = () -> {};

    public EditBox(TextRenderer textRenderer, int width) {
        this.textRenderer = textRenderer;
        this.width = width;
        this.setText("");
    }

    public void setChangeListener(Consumer<String> changeListener) {
        this.changeListener = changeListener;
    }

    public void setCursorChangeListener(Runnable cursorChangeListener) {
        this.cursorChangeListener = cursorChangeListener;
    }

    public void setText(String text) {
        this.text = text;
        this.cursor = this.text.length();
        this.selectionEnd = this.cursor;
        this.onChange();
    }

    public String getText() {
        return this.text;
    }

    public void replaceSelection(String string) {
        if (!string.isEmpty() || this.hasSelection()) {
            Substring substring = this.getSelection();
            this.text = (new StringBuilder(this.text)).replace(substring.beginIndex, substring.endIndex, string).toString();
            this.cursor = substring.beginIndex + string.length();
            this.selectionEnd = this.cursor;
            this.onChange();
        }
    }

    public void delete(int offset) {
        if (!this.hasSelection()) {
            this.selectionEnd = MathHelper.clamp(this.cursor + offset, 0, this.text.length());
        }

        this.replaceSelection("");
    }

    public int getCursor() {
        return this.cursor;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public Substring getSelection() {
        return new Substring(Math.min(this.selectionEnd, this.cursor), Math.max(this.selectionEnd, this.cursor));
    }

    public void surroundSelection(char opening, char closing) {
        if (hasSelection()) {
            int beforeIndex = getSelection().beginIndex;
            int afterIndex = getSelection().endIndex;
            replaceSelection(opening + getSelectedText() + closing);
            moveCursor(CursorMovement.ABSOLUTE, beforeIndex + 1);
            this.selectionEnd = afterIndex + 1;
        } else {
            if (this.cursor == this.text.length() || (this.cursor < this.text.length() && this.text.charAt(this.cursor) != closing)) {
                replaceSelection(String.valueOf(opening) + closing);
                moveCursor(CursorMovement.ABSOLUTE, getSelection().beginIndex - 1);
                this.selectionEnd = cursor;
            } else {
                replaceSelection(String.valueOf(opening));
            }
        }
    }

    public int getLineCount() {
        return this.lines.size();
    }

    public int getCurrentLineIndex() {
        for(int i = 0; i < this.lines.size(); ++i) {
            Substring substring = this.lines.get(i);
            if (this.cursor >= substring.beginIndex && this.cursor <= substring.endIndex) {
                return i;
            }
        }

        return -1;
    }

    public Substring getLine(int index) {
        return this.lines.get(MathHelper.clamp(index, 0, this.lines.size() - 1));
    }

    public void moveCursor(CursorMovement movement, int amount) {
        switch (movement) {
            case ABSOLUTE -> this.cursor = amount;
            case RELATIVE -> this.cursor += amount;
            case END -> this.cursor = this.text.length() + amount;
        }

        this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
        this.cursorChangeListener.run();
        if (!this.selecting) {
            this.selectionEnd = this.cursor;
        }

    }

    public void moveCursorLine(int offset) {
        if (offset != 0) {
            int i = this.textRenderer.getWidth(this.text.substring(this.getCurrentLine().beginIndex, this.cursor)) + 2;
            Substring substring = this.getOffsetLine(offset);
            int j = this.textRenderer.trimToWidth(this.text.substring(substring.beginIndex, substring.endIndex), i).length();
            this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex + j);
        }
    }

    public void moveCursor(double x, double y) {
        int i = MathHelper.floor(x);
        Objects.requireNonNull(this.textRenderer);
        int j = MathHelper.floor(y / 9.0);
        Substring substring = this.lines.get(MathHelper.clamp(j, 0, this.lines.size() - 1));
        int k = this.textRenderer.trimToWidth(this.text.substring(substring.beginIndex, substring.endIndex), i).length();
        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex + k);
    }

    public boolean handleSpecialKey(int keyCode) {
        this.selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.cursor = this.text.length();
            this.selectionEnd = 0;
            return true;
        } else if (Screen.isCopy(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        } else if (Screen.isPaste(keyCode)) {
            this.replaceSelection(MinecraftClient.getInstance().keyboard.getClipboard());
            return true;
        } else if (Screen.isCut(keyCode)) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            this.replaceSelection("");
            return true;
        } else {
            Substring substring;
            return switch (keyCode) {
                case 257, 335 -> {
                    this.replaceSelection("\n");
                    yield true;
                }
                case 259 -> {
                    if (Screen.hasControlDown()) {
                        substring = this.getPreviousWordAtCursor();
                        this.delete(substring.beginIndex - this.cursor);
                    } else {
                        // Deletes the next character too, if it's just like "{|}", cursor being |
                        if (cursor > 0 && text.length() > cursor) {
                            char beforeChar = text.charAt(cursor - 1);
                            char afterChar = text.charAt(cursor);
                            this.delete(-1);
                            if ((beforeChar == '{' && afterChar == '}') || (beforeChar == '[' && afterChar == ']')) {
                                this.delete(1);
                            }
                        } else {
                            this.delete(-1);
                        }
                    }
                    yield true;
                }
                case 261 -> {
                    if (Screen.hasControlDown()) {
                        substring = this.getNextWordAtCursor();
                        this.delete(substring.beginIndex - this.cursor);
                    } else {
                        this.delete(1);
                    }
                    yield true;
                }
                case 262 -> {
                    if (Screen.hasControlDown()) {
                        substring = this.getNextWordAtCursor();
                        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex);
                    } else {
                        this.moveCursor(CursorMovement.RELATIVE, 1);
                    }
                    yield true;
                }
                case 263 -> {
                    if (Screen.hasControlDown()) {
                        substring = this.getPreviousWordAtCursor();
                        this.moveCursor(CursorMovement.ABSOLUTE, substring.beginIndex);
                    } else {
                        this.moveCursor(CursorMovement.RELATIVE, -1);
                    }
                    yield true;
                }
                case 264 -> {
                    if (!Screen.hasControlDown()) {
                        this.moveCursorLine(1);
                    }
                    yield true;
                }
                case 265 -> {
                    if (!Screen.hasControlDown()) {
                        this.moveCursorLine(-1);
                    }
                    yield true;
                }
                case 266 -> {
                    this.moveCursor(CursorMovement.ABSOLUTE, 0);
                    yield true;
                }
                case 267 -> {
                    this.moveCursor(CursorMovement.END, 0);
                    yield true;
                }
                case 268 -> {
                    if (Screen.hasControlDown()) {
                        this.moveCursor(CursorMovement.ABSOLUTE, 0);
                    } else {
                        this.moveCursor(CursorMovement.ABSOLUTE, this.getCurrentLine().beginIndex);
                    }
                    yield true;
                }
                case 269 -> {
                    if (Screen.hasControlDown()) {
                        this.moveCursor(CursorMovement.END, 0);
                    } else {
                        this.moveCursor(CursorMovement.ABSOLUTE, this.getCurrentLine().endIndex);
                    }
                    yield true;
                }
                default -> false;
            };
        }
    }

    public Iterable<Substring> getLines() {
        return this.lines;
    }

    public boolean hasSelection() {
        return this.selectionEnd != this.cursor;
    }

    @VisibleForTesting
    public String getSelectedText() {
        Substring substring = this.getSelection();
        return this.text.substring(substring.beginIndex, substring.endIndex);
    }

    private Substring getCurrentLine() {
        return this.getOffsetLine(0);
    }

    private Substring getOffsetLine(int offsetFromCurrent) {
        int i = this.getCurrentLineIndex();
        if (i < 0) {
            int var10002 = this.cursor;
            throw new IllegalStateException("Cursor is not within text (cursor = " + var10002 + ", length = " + this.text.length() + ")");
        } else {
            return this.lines.get(MathHelper.clamp(i + offsetFromCurrent, 0, this.lines.size() - 1));
        }
    }

    @VisibleForTesting
    public Substring getPreviousWordAtCursor() {
        if (this.text.isEmpty()) {
            return EditBox.Substring.EMPTY;
        } else {
            int i;
            for(i = MathHelper.clamp(this.cursor, 0, this.text.length() - 1); i > 0 && Character.isWhitespace(this.text.charAt(i - 1)); --i) {
            }

            while(i > 0 && !Character.isWhitespace(this.text.charAt(i - 1))) {
                --i;
            }

            return new Substring(i, this.getWordEndIndex(i));
        }
    }

    @VisibleForTesting
    public Substring getNextWordAtCursor() {
        if (this.text.isEmpty()) {
            return EditBox.Substring.EMPTY;
        } else {
            int i;
            for(i = MathHelper.clamp(this.cursor, 0, this.text.length() - 1); i < this.text.length() && !Character.isWhitespace(this.text.charAt(i)); ++i) {
            }

            while(i < this.text.length() && Character.isWhitespace(this.text.charAt(i))) {
                ++i;
            }

            return new Substring(i, this.getWordEndIndex(i));
        }
    }

    private int getWordEndIndex(int startIndex) {
        int i;
        for(i = startIndex; i < this.text.length() && !Character.isWhitespace(this.text.charAt(i)); ++i) {
        }

        return i;
    }

    private void onChange() {
        this.rewrap();
        this.changeListener.accept(this.text);
        this.cursorChangeListener.run();
    }

    private void rewrap() {
        this.lines.clear();
        if (this.text.isEmpty()) {
            this.lines.add(EditBox.Substring.EMPTY);
        } else {
            this.textRenderer.getTextHandler().wrapLines(this.text, this.width, Style.EMPTY, false, (style, start, end) -> {
                this.lines.add(new Substring(start, end));
            });
            if (this.text.charAt(this.text.length() - 1) == '\n') {
                this.lines.add(new Substring(this.text.length(), this.text.length()));
            }

        }
    }

    @Environment(EnvType.CLIENT)
    protected record Substring(int beginIndex, int endIndex) {
        static final Substring EMPTY = new Substring(0, 0);

        public int beginIndex() {
            return beginIndex;
        }

        public int endIndex() {
            return endIndex;
        }
    }
}
