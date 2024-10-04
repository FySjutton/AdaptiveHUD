package ahud.adaptivehud.screens.configscreen;

import ahud.adaptivehud.ConfigFiles;
import ahud.adaptivehud.screens.movescreen.MoveScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.configFile;
import static ahud.adaptivehud.ConfigFiles.elementArray;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    public final List<JsonElement> BACKUP_ELEMENT_ARR = new ArrayList<>();
    private final List<String> DELETED_FILES = new ArrayList<>();
    private final Screen PARENT;

    private final Identifier DISCORD_TEXTURE = Identifier.of("adaptivehud", "textures/gui/discord_logo.png");
    private final Identifier SEARCH_ICON = Identifier.ofVanilla("icon/search");
    private final boolean renderDiscordButton = configFile.getAsJsonObject().get("render_get_help_button").getAsBoolean();

    private static final Text DISCORD_TEXT = Text.translatable("adaptivehud.config.discordText");
    private static final Text DEFAULT_NAME = Text.translatable("adaptivehud.config.defaultName");

    private ElementWidget elementWidget;
    private SearchBar searchBar;
    private Boolean fileChanged = false;
    private int discordWidth;

    public ConfigScreen(Screen parent) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = parent;

        for (JsonElement elm : elementArray) {
            BACKUP_ELEMENT_ARR.add(elm.deepCopy());
        }
    }

    @Override
    protected void init() {
        discordWidth = textRenderer.getWidth(DISCORD_TEXT) + 16 + 5 + 3 + 5;

        ButtonWidget newElm = ButtonWidget.builder(Text.translatable("adaptivehud.config.createNewElement"), btn -> createNewElement())
            .dimensions(width / 16, 50, width / 8 * 3, 20)
            .build();
        addDrawableChild(newElm);
        ButtonWidget moveElements = ButtonWidget.builder(Text.translatable("adaptivehud.config.moveElements"), btn -> moveElements())
                .dimensions(width / 16, 75, width / 8 * 3, 20)
                .build();
        addDrawableChild(moveElements);
        ButtonWidget reloadElements = ButtonWidget.builder(Text.translatable("adaptivehud.config.reloadElements"), btn -> reloadElements())
                .dimensions(width / 16, 100, (width / 8 * 3) / 3 * 2 - 5, 20)
                .build();
        addDrawableChild(reloadElements);
        ButtonWidget folderButton = ButtonWidget.builder(Text.translatable("adaptivehud.config.folder"), btn -> openFolder())
                .dimensions(width / 16 + (width / 8 * 3) / 3 * 2 - 5 + 5, 100, (width / 8 * 3) / 3, 20)
                .build();
        addDrawableChild(folderButton);
        ButtonWidget closeElm = ButtonWidget.builder(Text.translatable("adaptivehud.config.cancel"), btn -> discardChanges())
                .dimensions(width / 16, height - 50, width / 16 * 3 - 3, 20)
                .build();
        addDrawableChild(closeElm);
        ButtonWidget saveAndExitElm = ButtonWidget.builder(Text.translatable("adaptivehud.config.done"), btn -> saveAndExit())
                .dimensions(width / 4 + 3, height - 50, width / 16 * 3 - 3, 20)
                .build();
        addDrawableChild(saveAndExitElm);

        elementWidget = new ElementWidget(this, width, height);
        addDrawableChild(elementWidget);

        SearchBar searchBarElm = new SearchBar(textRenderer, width, elementWidget);
        addDrawableChild(searchBarElm);
        searchBar = searchBarElm;

        if (renderDiscordButton) {
            ButtonWidget discordButton = ButtonWidget.builder(Text.literal(""), btn -> openDiscord())
                    .dimensions(width - discordWidth - 18, 9, discordWidth, 20)
                    .build();
            addDrawableChild(discordButton);
        }

        changesMade();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, "AdaptiveHUD", width / 2, 15, 0xffffff);
        context.drawGuiTexture(SEARCH_ICON, width / 2 + 1, 34, 12, 12);

        if (renderDiscordButton) {
            context.drawTexture(DISCORD_TEXTURE, width - discordWidth - 13, 12, 0, 0, 14, 14, 14, 14);
            context.drawText(textRenderer, DISCORD_TEXT, width - discordWidth + 6, (int) (15.5), 0xFFFFFF, true);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (!searchBar.isFocused()) {
            setFocused(null);
        }
        return true;
    }

    @Override
    public void close() {
        client.setScreen(PARENT);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !fileChanged;
    }

    private void createNewElement() {
        try {
            InputStream resource = ConfigFiles.class.getResourceAsStream("/assets/adaptivehud/premade/new_element.json");
            String jsonContent = IOUtils.toString(resource, "UTF-8");
            JsonElement newElement = JsonParser.parseString(jsonContent);
            JsonObject newObject = newElement.getAsJsonObject();
            String newName = DEFAULT_NAME.getString().toLowerCase();
            int counter = 1;
            while (elementArray.toString().contains("\"name\":\"" + newName + "\",")) {
                newName = DEFAULT_NAME.getString().toLowerCase() + counter;
                counter++;
            }
            newObject.addProperty("name", newName);
            elementArray.add(newElement);
            elementWidget.updateElementList(null);
            searchBar.setText("");
            changesMade();
        } catch (Exception e) {
            LOGGER.error("Failed to create new element!");
            LOGGER.error(e.getMessage());
        }
    }

    private void discardChanges() {
        elementArray = BACKUP_ELEMENT_ARR;
        close();
    }

    private void saveAndExit() {
        if (fileChanged) {
            new ConfigFiles().saveElementFiles(elementArray, DELETED_FILES);
        }
        close();
    }

    private void reloadElements() {
        new ConfigFiles().GenerateElementArray();
        BACKUP_ELEMENT_ARR.clear();
        for (JsonElement elm : elementArray) {
            BACKUP_ELEMENT_ARR.add(elm.deepCopy());
        }
        elementWidget.updateElementList(null);
        searchBar.setText("");
    }

    private void moveElements() {
        MinecraftClient.getInstance().setScreen(new MoveScreen(this, false));
    }

    public void changesMade() {
        fileChanged = !elementArray.equals(BACKUP_ELEMENT_ARR);

        ButtonWidget reloadElementsElm = (ButtonWidget) children().get(2);
        ButtonWidget saveButtonElm = (ButtonWidget) children().get(5);
        ButtonWidget cancelButtonElm = (ButtonWidget) children().get(4);

        reloadElementsElm.active = !fileChanged;
        reloadElementsElm.setTooltip(fileChanged ? Tooltip.of(Text.translatable("adaptivehud.config.unsavedChanges")) : null);

        cancelButtonElm.active = fileChanged;
        cancelButtonElm.setTooltip(fileChanged ? Tooltip.of(Text.translatable("adaptivehud.config.cancelWarning")) : null);

        saveButtonElm.setMessage(Text.translatable("adaptivehud.config." + (fileChanged ? "save" : "done")));
    }

    public void deleteElement(JsonElement element) {
        elementArray.remove(element);
        String deleted_file_name = element.getAsJsonObject().get("name").getAsString();
        addDeletedFile(deleted_file_name);
        elementWidget.updateElementList(searchBar.getText());
        changesMade();
    }

    private void openDiscord() {
        try {
            Util.getOperatingSystem().open("https://discord.gg/tqn38v6w7k");
            LOGGER.info("Opening discord support server invite link in browser... (https://discord.gg/tqn38v6w7k)");
        } catch (Exception e) {
            LOGGER.error("Failed to open discord link! Link: https://discord.gg/tqn38v6w7k");
            LOGGER.error(String.valueOf(e));
        }
    }

    private void openFolder() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        File folder = new File(configDir + "/adaptivehud");
        Util.getOperatingSystem().open(folder);
    }

    public void addDeletedFile(String oldFileName) {
        if (!DELETED_FILES.contains(oldFileName)) {
            if (BACKUP_ELEMENT_ARR.toString().contains("\"name\":\"" + oldFileName + "\",")) {
                DELETED_FILES.add(oldFileName);
            }
        }
    }
}