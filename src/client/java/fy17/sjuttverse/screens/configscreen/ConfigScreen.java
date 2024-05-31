package fy17.sjuttverse.screens.configscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fy17.sjuttverse.ConfigFiles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    private final Screen parent;
    private final List<JsonElement> backupElementArr = new ArrayList<>();
    private Boolean fileChanged = false;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Sjuttverse"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        for (JsonElement elm : elementArray) {
            backupElementArr.add(elm.deepCopy());
        }

        ButtonWidget newElm = ButtonWidget.builder(Text.literal("Create new element"), btn -> createNewElement())
            .dimensions(width / 16, 50, width / 8 * 3, 20)
            .build();
        addDrawableChild(newElm);
        ButtonWidget reloadElements = ButtonWidget.builder(Text.literal("Reload element files"), btn -> reloadElements())
                .dimensions(width / 16, 75, width / 8 * 3, 20)
                .build();
        addDrawableChild(reloadElements);
        ButtonWidget closeElm = ButtonWidget.builder(Text.literal("Cancel"), btn -> discardChanges())
                .dimensions(width / 16, height - 50, width / 16 * 3 - 3, 20)
                .build();
        addDrawableChild(closeElm);
        closeElm.active = false;
        ButtonWidget saveAndExitElm = ButtonWidget.builder(Text.literal("Done"), btn -> saveAndExit())
                .dimensions(width / 4 + 3, height - 50, width / 16 * 3 - 3, 20)
                .build();
        addDrawableChild(saveAndExitElm);

        scrollableList = new ScrollableList(height, width, this);
        addDrawableChild(scrollableList);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Sjuttverse"), width / 2, 20, 0xffffff);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        setFocused(null);
        return true;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void createNewElement() {
        try {
            File defaultFile = new File("../src/client/resources/premade/new_element.json");
            JsonElement newElement = JsonParser.parseReader(new FileReader(defaultFile));
            JsonObject newObject = newElement.getAsJsonObject();
            String newName = "NewElement";
            int counter = 1;
            while (elementArray.toString().contains("\"name\":\"" + newName + "\",")) {
                newName = "NewElement" + counter;
                counter++;
            }
            newObject.addProperty("name", newName);
            elementArray.add(newElement);
            scrollableList.updateElementList(width);
            changesMade();
        } catch (FileNotFoundException e) {
            LOGGER.error("Failed to create new element!");
            LOGGER.error(String.valueOf(e));
        }
    }

    private void discardChanges() {
        elementArray = backupElementArr;
        close();
    }

    private void saveAndExit() {
        if (fileChanged) {
            new ConfigFiles().saveElementFiles(elementArray);
        }
        close();
    }

    private void reloadElements() {
        new ConfigFiles().GenerateElementArray();
        scrollableList.updateElementList(width);
    }

    public void changesMade() {
        fileChanged = true;

        ButtonWidget reloadElementsElm = (ButtonWidget) children().get(1);
        ButtonWidget saveButtonElm = (ButtonWidget) children().get(3);
        ButtonWidget cancelButtonElm = (ButtonWidget) children().get(2);

        reloadElementsElm.active = false;
        reloadElementsElm.setTooltip(Tooltip.of(Text.of("You have unsaved changes!")));
        cancelButtonElm.active = true;
        cancelButtonElm.setTooltip(Tooltip.of(Text.of("Your changes will not be saved!")));
        saveButtonElm.setMessage(Text.of("Save"));
    }

    public void deleteElement(JsonElement element, int width) {
        elementArray.remove(element);
        scrollableList.updateElementList(width);
        changesMade();
    }
}