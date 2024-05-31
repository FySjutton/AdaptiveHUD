package fy17.sjuttverse.screens.configscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    private final List<ButtonWidget> buttonList = new ArrayList<>();
    private final int buttonHeight = 20;
    private final int buttonMargin = 5;

    public ScrollableList(int height, int width) {
        super(width / 2, 45, width / 2 - 10, height - 50, Text.literal("Elements"));
        updateElementList(width);
    }

    public void updateElementList(int width) {
        buttonList.clear();
        int yPosition = 45 + buttonMargin;
        int boxWidth = width / 2 - 10;
        int boxPosX = width / 2;

        for (JsonElement element : elementArray) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> handleButtonClick(element.getAsJsonObject().get("name").getAsString()))
                    .dimensions(boxPosX + 5, yPosition, (int) ((boxWidth - 5) * 0.75), buttonHeight)
                    .build();
            buttonList.add(button);
            ButtonWidget enableButton = ButtonWidget.builder(Text.literal((element.getAsJsonObject().get("enabled").getAsBoolean() ? "On" : "Off")), btn -> switchEnabled(btn, element))
                    .dimensions((int) (boxPosX + (boxWidth - 5) * 0.75 + 10), yPosition, (boxWidth / 4 - 10), buttonHeight)
                    .build();
            buttonList.add(enableButton);
            yPosition += buttonHeight + buttonMargin;
        }
    }

    @Override
    protected int getContentsHeight() {
        return buttonList.size() / 2 * (buttonHeight + buttonMargin);
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

//        LOGGER.info(buttonList.toString());
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
    }

    private void switchEnabled(ButtonWidget button, JsonElement element) {
        JsonObject new_object = element.getAsJsonObject();
        if (button.getMessage().getString().equals("On")) {
            button.setMessage(Text.of("Off"));
            new_object.addProperty("enabled", false);
        } else {
            button.setMessage(Text.of("On"));
            new_object.addProperty("enabled", true);
        }
        elementArray.remove(element);
        elementArray.add(new_object);
    }
}
