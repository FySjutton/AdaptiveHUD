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
    private final List<ButtonWidget> BUTTON_LIST = new ArrayList<>();
    private final int BUTTON_HEIGHT = 20;
    private final int BUTTON_MARGIN = 5;
    private final ConfigScreen PARENT;
    private final String ON_TEXT = Text.translatable("adaptivehud.config.button.on").getString();
    private final String OFF_TEXT = Text.translatable("adaptivehud.config.button.off").getString();


    public ScrollableList(int height, int width, ConfigScreen parent) {
        super(width / 2, 45, width / 2 - 10, height - 50, Text.translatable("adaptivehud.config.title"));
        this.PARENT = parent;
        updateElementList(width);
    }

    public void updateElementList(int width) {
        BUTTON_LIST.clear();
        int yPosition = 45 + BUTTON_MARGIN;
        int boxWidth = width / 2 - 10;
        int boxPosX = width / 2;

        for (JsonElement element : elementArray) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> editElement(element))
                    .dimensions(boxPosX + 5, yPosition, (int) (0.6 * boxWidth - 12), BUTTON_HEIGHT)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.editElement")))
                    .build();
            BUTTON_LIST.add(button);
            ButtonWidget enableButton = ButtonWidget.builder(Text.literal((element.getAsJsonObject().get("enabled").getAsBoolean() ? ON_TEXT : OFF_TEXT)), btn -> switchEnabled(btn, element))
                    .dimensions((int) (boxPosX + 0.6 * boxWidth - 2), yPosition, (int) (0.25 * boxWidth - 5), BUTTON_HEIGHT)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.toggleElement")))
                    .build();
            BUTTON_LIST.add(enableButton);
            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("\uD83D\uDDD1"), btn -> this.PARENT.deleteElement(element, width))
                    .dimensions((int) (0.6 * boxPosX + 1.25 * boxWidth + 2), yPosition, (int) (0.15 * boxWidth - 3), BUTTON_HEIGHT)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.deleteElement")))
                    .build();
            BUTTON_LIST.add(deleteButton);
            yPosition += BUTTON_HEIGHT + BUTTON_MARGIN;
        }
    }

    @Override
    protected int getContentsHeight() {
        return BUTTON_LIST.size() / 3 * (BUTTON_HEIGHT + BUTTON_MARGIN);
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

        for (ButtonWidget buttonWidget : BUTTON_LIST) {
            int buttonTop = buttonWidget.getY();
            int buttonBottom = buttonTop + BUTTON_HEIGHT;

            if (buttonBottom >= startY && buttonTop <= endY) {
                buttonWidget.render(context, mouseX, (int) adjustedMouseY, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double adjustedMouseY = mouseY + getScrollY();
        for (ButtonWidget buttonWidget : BUTTON_LIST) {

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
        MinecraftClient.getInstance().setScreen(new ElementScreen(PARENT, element.getAsJsonObject()));
    }

    private void switchEnabled(ButtonWidget button, JsonElement element) {
        JsonObject new_object = element.getAsJsonObject();
        if (button.getMessage().getString().equals(ON_TEXT)) {
            button.setMessage(Text.of(OFF_TEXT));
            new_object.addProperty("enabled", false);
        } else {
            button.setMessage(Text.of(ON_TEXT));
            new_object.addProperty("enabled", true);
        }
        elementArray.set(elementArray.indexOf(element), new_object);
        PARENT.changesMade();
    }
}