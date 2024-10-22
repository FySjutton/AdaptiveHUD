package ahud.adaptivehud.screens.editscreen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Suggester {
    private final TextFieldWidget textField;
    private final TextRenderer textRenderer;
    private final List<String> suggestions = Lists.newArrayList();
    private int scroll = 0;
    private int highlight = 0;
    private final int maxSuggestions;

    private int maxLength = 0;

    private boolean hovering = false;

    private final int startX;
    private final int startY;

    public boolean visible = false;

    public Suggester(TextFieldWidget textField, TextRenderer textRenderer, int startX, int startY, int maxHeight) {
        this.textField = textField;
        this.textRenderer = textRenderer;
        this.startX = startX;
        this.startY = startY;
        this.maxSuggestions = maxHeight / 11;
    }

    public void updateSuggestions() {
        String searchContent = textField.getText();

        int oldScroll = scroll;
        scroll = 0;
        suggestions.clear();

        List<Integer> lengths = new ArrayList<>(List.of(0));

        // my amazing testing examples
        List<String> options = new ArrayList<>(List.of("abrabar", "bone", "cykel", "ckata", "björn", "bröd", "bakare", "brödrost", "abrööö", "broder", "brorsa", "brakar", "bäst", "byrne", "boskar", "brysk", "brask", "brösk"));
        for (String x : options) {
            if (x.contains(searchContent)) {
                suggestions.add(x);
                lengths.add(textRenderer.getWidth(x));
            }
        }

        if (oldScroll > suggestions.size() - maxSuggestions) {
            scroll = Math.max(suggestions.size() - maxSuggestions, 0);
        }

        maxLength = Collections.max(lengths);
    }

    public void render(DrawContext context) {
        int displayedSuggestions = Math.min(suggestions.size(), maxSuggestions);

        int y = startY - displayedSuggestions * 11 - 3;
        int y2 = y + displayedSuggestions * 11 + 3;
        int x = startX;
        int x2 = x + 90;

        if (displayedSuggestions > 0)  {
            context.fill(x, y, x2, y2, 0xcc000000);
        }

        if (scroll > 0) {
            for (int i = 0; i < x2 - x; i++) {
                if (i % 2 == 0) {
                    context.fill(x + i, y, x + i + 1, y + 1, -1);
                }
            }
        }

        if (scroll + displayedSuggestions < suggestions.size()) {
            for (int i = 0; i < x2 - x; i++) {
                if (i % 2 == 0) {
                    context.fill(x + i, y2 - 1, x + i + 1, y2, -1);
                }
            }
        }

        y += 2;
        for (int i = 0; i < displayedSuggestions; i++) {
            if (i == highlight) {
                context.drawTextWithShadow(textRenderer, suggestions.get(i + scroll), x + 3, y + 1, 0xffffe736);
            } else {
                context.drawTextWithShadow(textRenderer, suggestions.get(i + scroll), x + 3, y + 1, 0xFFFFFFFF);
            }
            y += 11;
        }
    }

    public void mouseMoved(double mouseX, double mouseY) {
        int displayedSuggestions = Math.min(suggestions.size(), maxSuggestions);
        int y = startY - displayedSuggestions * 11 - 3;
        if (mouseY > y && mouseY < y + displayedSuggestions * 11 + 3 && mouseX > startX && mouseX < startX + 90) {
            highlight = (int) Math.floor((mouseY - y) / 11);
            hovering = true;
        } else {
            hovering = false;
        }
    }

    public void mouseScrolled(boolean up, double mouseX, double mouseY) {
        int y = startY;
        int x = startX;
        int displayedSuggestions = Math.min(suggestions.size(), maxSuggestions);

        if (y - mouseY > 0 && mouseX > x && x + maxLength > mouseX && (y - mouseY) / 11 < displayedSuggestions) {
            if (scroll < suggestions.size() - maxSuggestions && !up) {
                scroll ++;
            } else if (up && scroll > 0) {
                scroll --;
            }
        }
    }

    public void mouseClicked() {
        if (hovering) {
            textField.setText(suggestions.get(scroll + highlight));
            highlight = 0;
        }
    }

    public boolean keyPressed(int keyCode) {
        if (keyCode == 258) {
            if (!suggestions.isEmpty()) {
                textField.setText(suggestions.get(scroll + highlight));
                highlight = 0;
                return false;
            }
        } if (keyCode == 264) {
            int lines = Math.min(suggestions.size(), maxSuggestions);
            if (highlight + 1 == lines && scroll + lines < suggestions.size()) {
                scroll ++;
            } else if (highlight + 1 < lines) {
                highlight ++;

            }
            return false;
        } else if (keyCode == 265) {
            if (highlight == 0 && scroll > 0) {
                scroll --;
            } else if (highlight > 0) {
                highlight --;
            }
            return false;
        }
        return true;
    }
}