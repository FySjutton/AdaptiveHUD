package ahud.adaptivehud.screens.elementscreen;

import ahud.adaptivehud.JsonValidator;
import ahud.adaptivehud.screens.configscreen.ConfigScreen;
import ahud.adaptivehud.Tools;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;
import static ahud.adaptivehud.ConfigFiles.elementArray;
import static ahud.adaptivehud.screens.elementscreen.SettingWidget.errors;

@Environment(EnvType.CLIENT)
public class ElementScreen extends Screen {
    public final Screen PARENT;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private JsonObject element;

    public ElementScreen(Screen PARENT, JsonObject element) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = PARENT;
        this.element = element;
    }

    @Override
    public void init() {
        Tab[] tabs = new Tab[2];
        tabs[0] = new newTab(
            this, "General",
            new ArrayList<>(List.of("enabled", "name")),
            new ArrayList<>(List.of(1, 2))
        );
        tabs[1] = new newTab(
            this, "Background",
            new ArrayList<>(List.of()),
            new ArrayList<>(List.of())
        );

        TabNavigationWidget tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(tabs).build();
        this.addDrawableChild(tabNavigation);

//        ButtonWidget saveButton = ButtonWidget.builder(Text.translatable("tab.bettertab.config.button_text.done"), btn -> saveFile()).dimensions(width / 4, height - 25, width / 2, 20).build();
//        this.addDrawableChild(saveButton);

        tabNavigation.selectTab(0, false);
        tabNavigation.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return errors.isEmpty();
    }

    @Override
    public void close() {
        client.setScreen(PARENT);
    }

    private class newTab extends GridScreenTab {
        public SettingWidget settingWidget;
        public newTab(Screen parent, String tabName, ArrayList<String> settings, ArrayList<Integer> types) {
            super(Text.of(tabName));
            GridWidget.Adder adder = grid.createAdder(1);

            settingWidget = new SettingWidget(parent, settings, types, element, width, height);
            adder.add(settingWidget);
        }
    }

//    private final JsonElement BEFORE_EDITING;
//    private static final String LEFT = Text.translatable("adaptivehud.config.button.left").getString();
//    private static final String RIGHT = Text.translatable("adaptivehud.config.button.right").getString();
//    private static final String TOP = Text.translatable("adaptivehud.config.button.top").getString();
//    private static final String BOTTOM = Text.translatable("adaptivehud.config.button.bottom").getString();
//    private static final String DOC_TEXT = Text.translatable("adaptivehud.config.documentation").getString();
//
//    private ScrollableArea scrollableArea;
//    public JsonObject elm;
//
//    public ElementScreen(Screen parent, JsonElement elm) {
//        super(Text.of("AdaptiveHUD"));
//        this.PARENT = parent;
//        this.elm = elm.deepCopy().getAsJsonObject();
//
//        BEFORE_EDITING = elm.deepCopy();
//    }
//
//    @Override
//    protected void init() {
//        ButtonWidget cancelBtn = ButtonWidget.builder(Text.translatable("adaptivehud.config.cancel"), btn -> close())
//            .dimensions(width / 2 - 105, height - 35, 100, 20)
//            .build();
//        addDrawableChild(cancelBtn);
//        ButtonWidget saveBtn = ButtonWidget.builder(Text.translatable("adaptivehud.config.save"), btn -> saveChanges())
//                .dimensions(width / 2 + 5, height - 35, 100, 20)
//                .build();
//        addDrawableChild(saveBtn);
//
//        ButtonWidget documentationButton = ButtonWidget.builder(Text.literal(DOC_TEXT), btn -> openDocumentation())
//                .dimensions(width - textRenderer.getWidth(DOC_TEXT) - 10 - 10, 20, textRenderer.getWidth(DOC_TEXT) + 10, 20)
//                .build();
//        addDrawableChild(documentationButton);
//
//        scrollableArea = new ScrollableArea(height, width, this);
//        addSelectableChild(scrollableArea);
//    }
//
//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//        scrollableArea.render(context, mouseX, mouseY, delta);
//    }
//
//    @Override
//    public boolean mouseReleased(double mouseX, double mouseY, int button) {
//        super.mouseReleased(mouseX, mouseY, button);
//        setFocused(null);
//        return true;
//    }
//
//    @Override
//    public void close() {
//        client.setScreen(PARENT);
//    }
//
//    @Override
//    public boolean shouldCloseOnEsc() {
//        return false;
//    }
//
//    @Override
//    public boolean charTyped(char chr, int keyCode) {
//        if (scrollableArea.charTyped(chr, keyCode)) {
//            return true;
//        }
//        return super.charTyped(chr, keyCode);
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (scrollableArea.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        }
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }
//
//    private void saveChanges() {
//        for (ScrollableArea.Entry x : scrollableArea.children()) {
//            if (x.title == null) {
//                JsonObject specElm;
//                if (x.setting.equals("enabled") || x.setting.equals("paddingX") || x.setting.equals("paddingY") || x.setting.equals("backgroundColor")) {
//                    specElm = elm.get("background").getAsJsonObject();
//                } else if (x.setting.equals("anchorPointX") || x.setting.equals("anchorPointY") || x.setting.equals("textAlignX") || x.setting.equals("textAlignY")) {
//                    specElm = elm.get("alignment").getAsJsonObject();
//                } else if (x.setting.equals("renderRequirement")) {
//                    specElm = elm.get("requirement").getAsJsonObject();
//                } else if (x.setting.equals("scale")) {
//                    specElm = elm.get("advanced").getAsJsonObject();
//                } else {
//                    specElm = elm;
//                }
//                if (x.textField != null) {
//                    if (x.setting.equals("posX") || x.setting.equals("posY") || x.setting.equals("paddingX")  || x.setting.equals("paddingY")) {
//                        try {
//                            specElm.addProperty(x.setting, Integer.parseInt(x.textField.getText()));
//                        } catch (Exception e) {
//                            specElm.addProperty(x.setting, x.textField.getText());
//                        }
//                    } else if (x.setting.equals("scale")) {
//                        try {
//                            specElm.addProperty(x.setting, Float.valueOf(x.textField.getText()));
//                        } catch (Exception e) {
//                            specElm.addProperty(x.setting, x.textField.getText());
//                        }
//                    } else if (x.setting.equals("name")) {
//                        specElm.addProperty(x.setting, x.textField.getText().toLowerCase());
//                    } else {
//                        specElm.addProperty(x.setting, x.textField.getText());
//                    }
//                }
//                if (x.button != null) {
//                    if (x.setting.equals("shadow") || x.setting.equals("enabled")) {
//                        specElm.addProperty(x.setting, x.button.getMessage().getString().equals(Text.translatable("adaptivehud.config.button.on").getString()));
//                    } else if (x.setting.equals("anchorPointX") || x.setting.equals("anchorPointY") || x.setting.equals("textAlignX") || x.setting.equals("textAlignY")) {
//                        String value = x.button.getMessage().getString();
//                        int corN = 1;
//                        if (value.equals(LEFT) || value.equals(TOP)) {
//                            corN = 0;
//                        } else if (value.equals(RIGHT) || value.equals(BOTTOM)) {
//                            corN = 2;
//                        }
//                        specElm.addProperty(x.setting, corN);
//                    }
//                }
//            }
//        }
//        String validated = new JsonValidator().validateElement(elm.getAsJsonObject());
//        if (validated == null) {
//            List<JsonElement> deepCopyArray = new ArrayList<>();
//            for (JsonElement elm : elementArray) {
//                deepCopyArray.add(elm.deepCopy());
//            }
//            deepCopyArray.remove(BEFORE_EDITING);
//            if (!deepCopyArray.toString().toLowerCase().contains("\"name\":\"" + elm.get("name").getAsString().toLowerCase() + "\",")) {
//                String old_file_name = BEFORE_EDITING.getAsJsonObject().get("name").getAsString();
//                if (!old_file_name.equals(elm.getAsJsonObject().get("name").getAsString())) {
//                    ((ConfigScreen) PARENT).addDeletedFile(old_file_name);
//                }
//
//                elementArray.set(elementArray.indexOf(BEFORE_EDITING.getAsJsonObject()), elm);
//                close();
//            } else {
//                new Tools().sendToast("§cInvalid!", "§fThe name must be unique!");
//            }
//        } else {
//            new Tools().sendToast("§cInvalid!", "§f" + validated);
//        }
//    }
//
//    private void openDocumentation() {
//        try {
//            Util.getOperatingSystem().open("https://adaptivehud.gitbook.io/");
//            LOGGER.info("Opening AdaptiveHUD documentation in browser... (https://adaptivehud.gitbook.io/)");
//        } catch (Exception e) {
//            LOGGER.error("Failed to open AdaptiveHUD documentation! Link: https://adaptivehud.gitbook.io/");
//            LOGGER.error(String.valueOf(e));
//        }
//    }
}