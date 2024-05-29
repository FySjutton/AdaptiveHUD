package fy17.sjuttverse.configscreen;

import com.google.gson.JsonElement;
import fy17.sjuttverse.ConfigFiles;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fy17.sjuttverse.ConfigFiles.elementArray;
import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class ScrollableList extends ScrollableWidget {
    private final List<ButtonWidget> buttonList;
    private final int buttonHeight = 20;
    private final int buttonMargin = 5;

    public ScrollableList(int height, int width) {
        super(width / 2, height / 2, width / 2 - 10, height / 2 - 5, Text.literal("Elements"));
        buttonList = new ArrayList<>();
        int yPosition = height / 2 + buttonMargin;

        Iterator<JsonElement> iterator = ConfigFiles.elementArray.iterator();
        for (JsonElement element : elementArray) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> handleButtonClick(element.getAsJsonObject().get("name").getAsString()))
                    .dimensions(width / 2 + 5, yPosition, width / 3, buttonHeight)
                    .build();
            buttonList.add(button);
            yPosition += buttonHeight + buttonMargin;
        }
    }

    @Override
    protected int getContentsHeight() {
        return buttonList.size() * (buttonHeight + buttonMargin);
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    public void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        double adjustedMouseY = mouseY + getScrollY();
        int startY = this.getY() + (int) getScrollY();
        int endY = startY + this.height;

        for (ButtonWidget buttonWidget : buttonList) {
            int buttonTop = buttonWidget.getY();
            int buttonBottom = buttonTop + buttonHeight;

            if (buttonBottom >= startY && buttonTop <= endY) {
                buttonWidget.render(context, mouseX, (int) adjustedMouseY, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double adjustedMouseY = mouseY + getScrollY();
        for (ButtonWidget buttonWidget : buttonList) {

            if (buttonWidget.isMouseOver(mouseX, adjustedMouseY)) {
                buttonWidget.onClick(mouseX, adjustedMouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    private void handleButtonClick(String str) {
        System.out.println("Button clicked: " + str);
    }
}
