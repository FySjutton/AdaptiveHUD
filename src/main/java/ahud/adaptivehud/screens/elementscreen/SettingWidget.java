package ahud.adaptivehud.screens.elementscreen;

import ahud.adaptivehud.SettingText;
import ahud.adaptivehud.Tools;
import ahud.adaptivehud.screens.configscreen.ConfigScreen;
import ahud.adaptivehud.screens.configscreen.ScrollableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ahud.adaptivehud.ConfigFiles.elementArray;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class SettingWidget extends ElementListWidget<SettingWidget.Entry> {
    private Screen PARENT;
    private final TextRenderer textRenderer = client.textRenderer;
    private JsonObject element;
    public static ArrayList<String> errors = new ArrayList<>();

    public SettingWidget(Screen parent, ArrayList<String> settings, ArrayList<Integer> types, JsonObject element, int width, int height) {
        super(MinecraftClient.getInstance(), width, height - 24 - 25 - 10, 24, 25);
        this.PARENT = parent;
        this.element = element;

        for (int i = 0; i < settings.size(); i++) {
            addEntry(new Entry(settings.get(i), types.get(i)));
        }
    }

    @Override
    protected int getScrollbarX() {
        return width - 15;
    }

    @Override
    public int getRowWidth() {
        return width - 15;
    }

    public class Entry extends ElementListWidget.Entry<SettingWidget.Entry> {
        public SettingText textField;
        public ButtonWidget button;
        public String setting;
        public Text displayText;

        public Entry(String item, int type) {
            this.setting = item;
            this.displayText = Text.translatable("adaptivehud.config.setting." + item);
            if (type == 1) { // 1 = Boolean Button
                this.button = ButtonWidget.builder(Text.empty(), btn -> toggleElement(setting, btn))
                    .dimensions(width / 2 + width / 4 - 50, 0, 100, 20)
                    .build();
                updateBooleanValue(setting, button);
            } else if (type == 2) { // 2 = Text Field
                this.textField = new SettingText(textRenderer, width / 2 + width / 4 - 50, 0, 100, 20, Text.of(setting));
                if (setting.equals("value")) {
                    textField.setMaxLength(1000);
                }
                textField.setChangedListener(newValue -> textFieldListener(newValue, setting, textField));
                updateTextFieldText(setting, textField);
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            List<Selectable> children = new ArrayList<>();
            if (button != null) {
                children.add(button);
            }
            if (textField != null) {
                children.add(textField);
            }
            return children;
        }

        @Override
        public List<? extends net.minecraft.client.gui.Element> children() {
            List<Element> children = new ArrayList<>();
            if (button != null) {
                children.add(button);
            }
            if (textField != null) {
                children.add(textField);
            }
            return children;
        }

        @Override
        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (this.textField != null) {
                this.textField.setY(y);
                this.textField.render(drawContext, mouseX, mouseY, tickDelta);
                drawContext.drawText(textRenderer, displayText, width / 2 - 150, y + entryHeight / 2 - textRenderer.fontHeight / 2, textField.error ? 0xff4f4f : 0xFFFFFF, true);
            }
            if (this.button != null) {
                this.button.setY(y);
                this.button.render(drawContext, mouseX, mouseY, tickDelta);
                drawContext.drawText(textRenderer, displayText, width / 2 - 150, y + entryHeight / 2 - textRenderer.fontHeight / 2, 0xFFFFFF, true);
            }
        }
    }

    private void toggleElement(String setting, ButtonWidget button) {
        element.addProperty(setting, !element.get(setting).getAsBoolean());
        updateBooleanValue(setting, button);
    }

    private void updateBooleanValue(String setting, ButtonWidget button) {
        button.setMessage(Text.of(element.get(setting).getAsBoolean() ? "On" : "Off"));
    }

    private void updateTextFieldText(String setting, SettingText textField) {
        textField.setText(element.get(setting).getAsString());
    }

    private void textFieldListener(String newValue, String setting, SettingText textField) {
        // FIX SO THE DONE BUTTON CAN'T BE PRESSED ON ERROR; INACTIVATE
        if (setting.equals("name")) {
            // IMPROVE HAHA, FEELS INEFFICIENT
            if (newValue.contains(" ") || newValue.isEmpty()) {
                textField.setError(true, "Invalid name!");
                return;
            };
            List<JsonElement> deepCopyArray = elementArray.stream()
                    .map(JsonElement::deepCopy)
                    .collect(Collectors.toList());

            deepCopyArray.remove(element);

            // Checks if there's already an element with that same name
            boolean hasSameName = deepCopyArray.stream().anyMatch(elm -> elm.getAsJsonObject().get("name").getAsString().equals(newValue));
            if (hasSameName) {
                textField.setError(true, "Name already defined!");
                return;
            }
        }

        if (setting.equals("textColor")) {
            if (new Tools().parseColor(newValue) == null) {
                textField.setError(true, "Invalid Color!");
                return;
            }
        }

        textField.setError(false, null);
        element.addProperty(setting, newValue);
    }
}
