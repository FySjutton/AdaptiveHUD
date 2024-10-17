package ahud.adaptivehud.screens.editscreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class Suggestor {
    private final TextFieldWidget textField;
    private final TextRenderer textRenderer;
    private List<String> suggestions = Lists.newArrayList();
    private int scroll = 0;
    private int highlight = 0;
    private final int maxSuggestions;

    private int maxLength = 0;

    private boolean hovering = false;

    private final int startX;
    private final int startY;

    public Suggestor(TextFieldWidget textField, TextRenderer textRenderer, int startX, int startY, int maxHeight) {
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
        for (String x : List.of("abrabar", "bone", "cykel", "ckata", "björn", "bröd", "bakare", "brödrost", "abrööö", "broder", "brorsa", "brakar", "bäst", "byrne", "boskar", "brysk", "brask", "brösk")) {
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

    public void render(DrawContext context, double mouseX, double mouseY) {
        int y = startY;
        int x = startX + 3;

        int displayedSuggestions = Math.min(suggestions.size(), maxSuggestions);
        boolean scrollUp = scroll + displayedSuggestions < suggestions.size();
        String seperatorString = ".".repeat((maxLength + 1) / textRenderer.getWidth("."));


        if (displayedSuggestions > 0) {
            context.fill(x - 3, y, x + maxLength + 3, y - displayedSuggestions * 11 - 5 - (scroll > 0 ? 5 : 0) - (scrollUp ? 5 : 0), 0xcc000000);
        }

        if (scroll > 0) {
            context.drawTextWithShadow(textRenderer, seperatorString, x, y - 11, 0xFFFFFFFF);
            y -= 5;
        }

        for (int i = 0; i < displayedSuggestions; i++) {
            if (i == highlight) {
                context.drawTextWithShadow(textRenderer, suggestions.get(i + scroll), x, y - 11, 0xffffe736);
            } else {
                context.drawTextWithShadow(textRenderer, suggestions.get(i + scroll), x, y - 11, 0xFFFFFFFF);
            }
            y -= 11;
        }
        if (scrollUp) {
            context.drawTextWithShadow(textRenderer, seperatorString, x, y - 10, 0xFFFFFFFF);
        }
    }

    public void mouseMoved(double mouseX, double mouseY) {
        int displayedSuggestions = Math.min(suggestions.size(), maxSuggestions);

        if (startY - mouseY > 0 && mouseX > startX && startX + maxLength > mouseX && (startY - mouseY) / 11 < displayedSuggestions) {
            highlight = (int) Math.ceil((startY - mouseY) / 11) - 1;
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
            if (scroll < suggestions.size() - maxSuggestions && up) {
                scroll ++;
            } else if (!up && scroll > 0) {
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
            if (highlight == 0 && scroll > 0) {
                scroll --;
            } else if (scroll > 0 && highlight > 0) {
                highlight --;
            }
            return false;
        } else if (keyCode == 265) {
            if (highlight + scroll < suggestions.size() && highlight + 1 < maxSuggestions) {
                highlight ++;
            } else if (scroll + maxSuggestions < suggestions.size()) {
                scroll ++;
            }
            return false;
        }
        return true;
    }
}