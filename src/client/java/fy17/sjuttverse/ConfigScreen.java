package fy17.sjuttverse;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.literal("My tutorial screen"));
    }

    private List<String> stringList;
    private List<ButtonWidget> buttonList;
    private int buttonHeight = 25; // Height of each button
    private int buttonMargin = 5; // Margin between buttons

    @Override
    protected void init() {
        // Create a list of 30 strings
        stringList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            stringList.add("String " + i);
        }
        buttonList = new ArrayList<>();

        int widgetHeight = height / 2; // 50% of screen height
        int widgetYPosition = height - widgetHeight - buttonMargin; // Place at the bottom with a margin

        int yPosition = widgetYPosition + buttonMargin; // Start position for the first button, inside the scrollable area
        for (String str : stringList) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(str), btn -> handleButtonClick(str))
                    .dimensions(10, yPosition, 180, buttonHeight)
                    .tooltip(Tooltip.of(Text.literal("Tooltip for " + str)))
                    .build();
            buttonList.add(button);
            yPosition += buttonHeight + buttonMargin; // Next button below the previous one
        }

        ScrollableWidget scrollableWidget = new ScrollableWidget(5, widgetYPosition, width - 10, widgetHeight, Text.literal("Scrollable Area")) {
            @Override
            protected int getContentsHeight() {
                // Height of the content in the scrollable area (adjusted for the number of buttons)
                return stringList.size() * (buttonHeight + buttonMargin);
            }

            @Override
            protected double getDeltaYPerScroll() {
                return 10; // Adjust scroll speed
            }

            @Override
            protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
                int yPosition = (int) -getScrollY() + widgetYPosition + buttonMargin; // Adjust with scroll position and widget position
                for (ButtonWidget button : buttonList) {
                    button.setY(yPosition); // Set button's y position
                    button.render(context, mouseX, mouseY, delta);
                    yPosition += buttonHeight + buttonMargin; // Next button below the previous one
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                double adjustedMouseY = mouseY + getScrollY(); // Adjust with scroll position
                for (ButtonWidget buttonWidget : buttonList) {
                    if (buttonWidget.isMouseOver(mouseX, adjustedMouseY)) {
                        buttonWidget.onClick(mouseX, mouseY);
                        return true;
                    }
                }
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {
                // Add any necessary narrations
            }
        };

        addDrawableChild(scrollableWidget);
    }

    private void handleButtonClick(String str) {
        System.out.println("Button clicked: " + str);
    }
}






