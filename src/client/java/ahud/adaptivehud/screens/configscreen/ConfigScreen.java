package ahud.adaptivehud.screens.configscreen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ahud.adaptivehud.ConfigFiles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.elementArray;
import static ahud.adaptivehud.adaptivehud.LOGGER;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private ScrollableList scrollableList;
    private final Screen parent;
    private final List<JsonElement> backupElementArr = new ArrayList<>();
    private Boolean fileChanged = false;
    private final Identifier discordTexture = new Identifier("adaptivehud", "textures/gui/discord_logo.png");
    private int discordWidth;
    public final List<String> deletedFiles = new ArrayList<>();

    public ConfigScreen(Screen parent) {
        super(Text.literal("AdaptiveHUD"));
        this.parent = parent;

        for (JsonElement elm : elementArray) {
            backupElementArr.add(elm.deepCopy());
        }
    }

    @Override
    protected void init() {
        discordWidth = textRenderer.getWidth("Get help here!") + 16 + 5 + 3 + 5;

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
        ButtonWidget saveAndExitElm = ButtonWidget.builder(Text.literal("Done"), btn -> saveAndExit())
                .dimensions(width / 4 + 3, height - 50, width / 16 * 3 - 3, 20)
                .build();
        addDrawableChild(saveAndExitElm);

        ButtonWidget discordButton = ButtonWidget.builder(Text.literal(""), btn -> openDiscord())
                .dimensions(width - discordWidth - 10, 20, discordWidth, 20)
                .build();
        addDrawableChild(discordButton);

        scrollableList = new ScrollableList(height, width, this);
        addDrawableChild(scrollableList);

        changesMade();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("AdaptiveHUD"), width / 2, 20, 0xffffff);

        context.drawTexture(discordTexture, width - discordWidth - 5, 23, 0, 0, 14, 14, 14, 14);
        context.drawText(textRenderer, "Get help here!", width - discordWidth + 14, (int) (26.5), 0xFFFFFF, true);
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
        return !fileChanged;
    }

    private void createNewElement() {
        try {
            URL resource = ConfigScreen.class.getResource("/assets/premade/new_element.json");
            File resourceFile = Paths.get(resource.toURI()).toFile();
            JsonElement newElement = JsonParser.parseReader(new FileReader(resourceFile));
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
        } catch (Exception e) {
            LOGGER.error("Failed to create new element!");
            LOGGER.error(e.getMessage());
        }
    }

    private void discardChanges() {
        elementArray = backupElementArr;
        close();
    }

    private void saveAndExit() {
        if (fileChanged) {
            new ConfigFiles().saveElementFiles(elementArray, deletedFiles);
        }
        close();
    }

    private void reloadElements() {
        new ConfigFiles().GenerateElementArray();
        backupElementArr.clear();
        for (JsonElement elm : elementArray) {
            backupElementArr.add(elm.deepCopy());
        }
        scrollableList.updateElementList(width);
    }

    public void changesMade() {
        fileChanged = !elementArray.equals(backupElementArr);

        ButtonWidget reloadElementsElm = (ButtonWidget) children().get(1);
        ButtonWidget saveButtonElm = (ButtonWidget) children().get(3);
        ButtonWidget cancelButtonElm = (ButtonWidget) children().get(2);

        reloadElementsElm.active = !fileChanged;
        reloadElementsElm.setTooltip(fileChanged ? Tooltip.of(Text.of("You have unsaved changes!")) : null);

        cancelButtonElm.active = fileChanged;
        cancelButtonElm.setTooltip(fileChanged ? Tooltip.of(Text.of("Your changes will not be saved!")) : null);

        saveButtonElm.setMessage(Text.of(fileChanged ? "Save" : "Done"));
    }

    public void deleteElement(JsonElement element, int width) {
        elementArray.remove(element);
        String deleted_file_name = element.getAsJsonObject().get("name").getAsString();
        addDeletedFile(deleted_file_name);
        scrollableList.updateElementList(width);
        changesMade();
    }

    private void openDiscord() {
        try {
            Desktop.getDesktop().browse(new URI("https://discord.gg/tqn38v6w7k"));
            LOGGER.info("Opening discord support server invite link in browser... (https://discord.gg/tqn38v6w7k)");
        } catch (Exception e) {
            LOGGER.error("Failed to open discord link! Link: https://discord.gg/tqn38v6w7k");
            LOGGER.error(String.valueOf(e));
        }
    }

    public void addDeletedFile(String oldFileName) {
        if (!deletedFiles.contains(oldFileName)) {
            if (backupElementArr.toString().contains("\"name\":\"" + oldFileName + "\",")) {
                deletedFiles.add(oldFileName);
            }
        }
    }
}