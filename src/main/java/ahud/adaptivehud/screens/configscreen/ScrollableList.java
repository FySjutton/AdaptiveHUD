package ahud.adaptivehud.screens.configscreen;

import ahud.adaptivehud.screens.elementscreen.ElementScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.elementArray;

public class ScrollableList extends ScrollableWidget {
    private final List<ButtonWidget> buttonList = new ArrayList<>();
    private final int buttonHeight = 20;
    private final int buttonMargin = 5;
    private final ConfigScreen parent;
    private final String ONTEXT = Text.translatable("adaptivehud.config.button.on").getString();
    private final String OFFTEXT = Text.translatable("adaptivehud.config.button.off").getString();


    public ScrollableList(int height, int width, ConfigScreen parent) {
        super(width / 2, 45, width / 2 - 10, height - 50, Text.translatable("adaptivehud.config.title"));
        this.parent = parent;
        updateElementList(width);
    }

    public void updateElementList(int width) {
        buttonList.clear();
        int yPosition = 45 + buttonMargin;
        int boxWidth = width / 2 - 10;
        int boxPosX = width / 2;

        for (JsonElement element : elementArray) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> editElement(element))
                    .dimensions(boxPosX + 5, yPosition, (int) (0.6 * boxWidth - 12), buttonHeight)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.editElement")))
                    .build();
            buttonList.add(button);
            ButtonWidget enableButton = ButtonWidget.builder(Text.literal((element.getAsJsonObject().get("enabled").getAsBoolean() ? ONTEXT : OFFTEXT)), btn -> switchEnabled(btn, element))
                    .dimensions((int) (boxPosX + 0.6 * boxWidth - 2), yPosition, (int) (0.25 * boxWidth - 5), buttonHeight)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.toggleElement")))
                    .build();
            buttonList.add(enableButton);
            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("\uD83D\uDDD1"), btn -> this.parent.deleteElement(element, width))
                    .dimensions((int) (0.6 * boxPosX + 1.25 * boxWidth + 2), yPosition, (int) (0.15 * boxWidth - 3), buttonHeight)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.deleteElement")))
                    .build();
            buttonList.add(deleteButton);
            yPosition += buttonHeight + buttonMargin;
        }
    }

    @Override
    protected int getContentsHeight() {
        return buttonList.size() / 3 * (buttonHeight + buttonMargin);
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private void editElement(JsonElement element) {
        MinecraftClient.getInstance().setScreen(new ElementScreen(parent, element));
    }

    private void switchEnabled(ButtonWidget button, JsonElement element) {
        JsonObject new_object = element.getAsJsonObject();
        if (button.getMessage().getString().equals(ONTEXT)) {
            button.setMessage(Text.of(OFFTEXT));
            new_object.addProperty("enabled", false);
        } else {
            button.setMessage(Text.of(ONTEXT));
            new_object.addProperty("enabled", true);
        }
        elementArray.set(elementArray.indexOf(element), new_object);
        parent.changesMade();
    }
}