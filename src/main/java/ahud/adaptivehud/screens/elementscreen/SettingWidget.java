package ahud.adaptivehud.screens.elementscreen;

import ahud.adaptivehud.JsonValidator;
import ahud.adaptivehud.screens.editscreen.EditScreen;
import ahud.adaptivehud.screens.widgets.CustomTextField;
import ahud.adaptivehud.screens.widgets.CustomButton;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SettingWidget extends ElementListWidget<SettingWidget.Entry> {
    public Screen PARENT;
    private final TextRenderer textRenderer = client.textRenderer;
    private final JsonObject element;

    private final JsonValidator validator = new JsonValidator();

    public SettingWidget(Screen parent, String tabInt, ArrayList<String> settings, ArrayList<Integer> types, JsonObject element, int width, int height) {
        super(MinecraftClient.getInstance(), width, height - 24 - 25 - 10, 24, 25);
        this.PARENT = parent;
        if (tabInt == null) {
            this.element = element;
        } else {
            this.element = element.getAsJsonObject().get(tabInt).getAsJsonObject();
        }

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
        public CustomTextField textField;
        public CustomButton button;
        public String setting;
        public Text displayText;

        public Entry(String item, int type) {
            this.setting = item;
            this.displayText = Text.translatable("adaptivehud.config.setting." + item);
            if (type == 1 || (type >= 6 && type <= 8)) { // 1, 6-8 = Button
                this.button = CustomButton.customBuilder(Text.empty(), btn -> buttonPress(setting, this.button))
                    .dimensions(width / 2 + width / 4 - 50, 0, 100, 20)
                    .build();
                this.button.type = type;
                updateButtonValue(setting, button);
            } else if ((type >= 2 && type <= 5) || type == 9) { // 2 - 5 = Text Field
                this.textField = new CustomTextField(textRenderer, width / 2 + width / 4 - 50, 0, 100, 20, Text.of(setting), SettingWidget.this);
                this.textField.type = type;
                this.textField.setChangedListener(newValue -> textFieldListener(newValue, setting, this.textField));
                if (type == 3) {
                    this.textField.setMaxLength(16);
                }
                updateTextFieldText(setting, this.textField);
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

    private void buttonPress(String setting, CustomButton button) {
        if (button.type == 1) {
            element.addProperty(setting, !element.get(setting).getAsBoolean());
        } else if (button.type == 7 || button.type == 8) {
            // [7] 0: Left, 1: Center, 2: Right
            // [8] 0: Top, 1: Center, 2: Bottom
            int current = element.get(setting).getAsInt();
            if (current == 0) {
                element.addProperty(setting, 1);
            } else {
                element.addProperty(setting, current == 1 ? 2 : 0);
            }
        } else if (button.type == 6) {
            // Open the value screen, this is like the first comment i've made, i'll start being better :((((
            client.setScreen(new EditScreen(this, element));
            return;
        }
        updateButtonValue(setting, button);
    }

    private void updateButtonValue(String setting, CustomButton button) {
        if (button.type == 1) {
            button.setMessage(Text.translatable("adaptivehud.config.button." + (element.get(setting).getAsBoolean() ? "on" : "off")));
        } else if (button.type == 7) {
            int current = element.get(setting).getAsInt();
            if (current == 0) {
                button.setMessage(Text.translatable("adaptivehud.config.button.left"));
            } else if (current == 1) {
                button.setMessage(Text.translatable("adaptivehud.config.button.center"));
            } else {
                button.setMessage(Text.translatable("adaptivehud.config.button.right"));
            }
        } else if (button.type == 8) {
            int current = element.get(setting).getAsInt();
            if (current == 0) {
                button.setMessage(Text.translatable("adaptivehud.config.button.top"));
            } else if (current == 1) {
                button.setMessage(Text.translatable("adaptivehud.config.button.center"));
            } else {
                button.setMessage(Text.translatable("adaptivehud.config.button.bottom"));
            }
        } else if (button.type == 6) {
            button.setMessage(Text.translatable("adaptivehud.config.button.changeValue"));
        }
    }

    private void updateTextFieldText(String setting, CustomTextField textField) {
        textField.setText(element.get(setting).getAsString());
    }

    private void textFieldListener(String newValue, String setting, CustomTextField textField) {
        if (textField.type == 3) {
            String nameError = new JsonValidator().validateName(element, newValue, false);
            if (nameError != null) {
                textField.setError(true, nameError);
                return;
            }
        } else if (textField.type == 4) {
            String color = validator.validateColor(newValue);
            if (color != null) {
                textField.setError(true, color);
                return;
            }
        } else if (textField.type == 5) {
            try {
                Integer.parseInt(newValue);
            } catch (Exception ignored) {
                textField.setError(true, Text.translatable("adaptivehud.config.error.invalid_number").getString());
                return;
            }
        } else if (textField.type == 9) {
            String scale = validator.validateScale(newValue);
            if (scale != null) {
                textField.setError(true, scale);
                return;
            }
        }

        textField.setError(false, null);
        if (textField.type == 9 && newValue.isEmpty()) {
            element.addProperty(setting, "0");
        } else {
            element.addProperty(setting, newValue);
        }
    }
}
