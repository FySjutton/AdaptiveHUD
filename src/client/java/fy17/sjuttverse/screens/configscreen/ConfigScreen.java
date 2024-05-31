package fy17.sjuttverse.screens.configscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static fy17.sjuttverse.ConfigFiles.elementArray;
import static fy17.sjuttverse.Sjuttverse.LOGGER;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private ScrollableList scrollableList;

    public ConfigScreen() {
        super(Text.literal("Sjuttverse"));
    }

    @Override
    protected void init() {
        scrollableList = new ScrollableList(height, width);
        addDrawableChild(scrollableList);

        ButtonWidget button = ButtonWidget.builder(Text.literal("Create new element"), btn -> createNewElement())
            .dimensions(width / 8, 50, width / 4, 20)
            .build();

        addDrawableChild(button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Sjuttverse - Elements"), width / 2, 20, 0xffffff);
    }

    private void createNewElement() {
        try {
            File defaultFile = new File("../src/client/resources/premade/new_element.json");
            JsonElement newElement = JsonParser.parseReader(new FileReader(defaultFile));
            elementArray.add(newElement);
            scrollableList.updateElementList(width);
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to create new element!");
            LOGGER.error(String.valueOf(e));
        }
    }
}