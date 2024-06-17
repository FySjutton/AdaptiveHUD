package ahud.adaptivehud.screens.elementscreen;

import ahud.adaptivehud.jsonValidator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ahud.adaptivehud.ConfigFiles;
import ahud.adaptivehud.screens.configscreen.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.elementArray;
import static ahud.adaptivehud.adaptivehud.LOGGER;

@Environment(EnvType.CLIENT)
public class ElementScreen extends Screen {
    private final Screen parent;
    private ScrollableArea scrollableArea;
    private final JsonElement beforeEditing;
    public JsonObject elm;

    public ElementScreen(Screen parent, JsonElement elm) {
        super(Text.literal("AdaptiveHUD"));
        this.parent = parent;
        this.elm = elm.deepCopy().getAsJsonObject();

        beforeEditing = elm.deepCopy();
    }

    @Override
    protected void init() {
        ButtonWidget cancelBtn = ButtonWidget.builder(Text.literal("Cancel"), btn -> close())
            .dimensions(width / 2 - 105, height - 35, 100, 20)
            .build();
        addDrawableChild(cancelBtn);
        ButtonWidget saveBtn = ButtonWidget.builder(Text.literal("Save"), btn -> saveChanges())
                .dimensions(width / 2 + 5, height - 35, 100, 20)
                .build();
        addDrawableChild(saveBtn);

        scrollableArea = new ScrollableArea(height, width, this);
        addSelectableChild(scrollableArea);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
        scrollableArea.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

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

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (scrollableArea.charTyped(chr, keyCode)) {
            return true;
        }
        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (scrollableArea.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveChanges() {
        for (ScrollableArea.Entry x : scrollableArea.children()) {
            if (x.title == null) {
                JsonObject specElm;
                if (x.setting.equals("enabled") || x.setting.equals("paddingX") || x.setting.equals("paddingY") || x.setting.equals("backgroundColor")) {
                    specElm = elm.get("background").getAsJsonObject();
                } else if (x.setting.equals("anchorPointX") || x.setting.equals("anchorPointY") || x.setting.equals("textAlignX") || x.setting.equals("textAlignY")) {
                    specElm = elm.get("alignment").getAsJsonObject();
                } else {
                    specElm = elm;
                }
                if (x.textField != null) {
                    if (x.setting.equals("posX") || x.setting.equals("posY") || x.setting.equals("paddingX")  || x.setting.equals("paddingY")) {
                        try {
                            specElm.addProperty(x.setting, Integer.parseInt(x.textField.getText()));
                        } catch (Exception e) {
                            specElm.addProperty(x.setting, x.textField.getText());
                        }
                    } else if (x.setting.equals("name")) {
                        specElm.addProperty(x.setting, x.textField.getText().toLowerCase());
                    } else {
                        specElm.addProperty(x.setting, x.textField.getText());
                    }
                }
                if (x.button != null) {
                    if (x.setting.equals("shadow") || x.setting.equals("enabled")) {
                        specElm.addProperty(x.setting, x.button.getMessage().getString().equals("On"));
                    } else {
                        specElm.addProperty(x.setting, x.button.getMessage().getString());
                    }
                }
            }
        }
        String validated = new jsonValidator().validateElement(elm.getAsJsonObject());
        if (validated == null) {
            List<JsonElement> deepCopyArray = new ArrayList<>();
            for (JsonElement elm : elementArray) {
                deepCopyArray.add(elm.deepCopy());
            }
            deepCopyArray.remove(beforeEditing);
            if (!deepCopyArray.toString().toLowerCase().contains("\"name\":\"" + elm.get("name").getAsString().toLowerCase() + "\",")) {
                String old_file_name = beforeEditing.getAsJsonObject().get("name").getAsString();
                if (!old_file_name.equals(elm.getAsJsonObject().get("name").getAsString())) {
                    ((ConfigScreen) parent).addDeletedFile(old_file_name);
                }

                elementArray.set(elementArray.indexOf(beforeEditing.getAsJsonObject()), elm);
                close();
            } else {
                new ConfigFiles().sendToast("§cInvalid!", "§fThe name must be unique!");
            }
        } else {
            new ConfigFiles().sendToast("§cInvalid!", "§f" + validated);
        }
    }
}