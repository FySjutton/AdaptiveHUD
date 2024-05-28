package fy17.sjuttverse.configscreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private List<String> stringList;
    private List<ButtonWidget> buttonList;
    private int buttonHeight = 25;
    private int buttonMargin = 5;

    public ConfigScreen() {
        super(Text.literal("Sjuttverse"));
    }

    @Override
    protected void init() {
        stringList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            stringList.add("String " + i);
        }
        buttonList = new ArrayList<>();

        int widgetHeight = height / 2;
        int widgetYPosition = height - widgetHeight - buttonMargin;

        int yPosition = widgetYPosition + buttonMargin;
        for (String str : stringList) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(str), btn -> handleButtonClick(str))
                    .dimensions(width / 2 + 5, yPosition, width / 3, buttonHeight)
                    .tooltip(Tooltip.of(Text.literal("Tooltip for " + str)))
                    .build();
            buttonList.add(button);
            yPosition += buttonHeight + buttonMargin;
        }

        ScrollableList scrollableList = new ScrollableList(width / 2 - 5, widgetYPosition, (width - 5) / 2, widgetHeight, stringList, buttonList, buttonHeight, buttonMargin);
        addDrawableChild(scrollableList);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Sjuttverse - Elements"), width / 2, 20, 0xffffff);
    }

    private void handleButtonClick(String str) {
        System.out.println("Button clicked: " + str);
    }
}
