package ahud.adaptivehud.screens.elementscreen.widgets;

import ahud.adaptivehud.screens.elementscreen.ElementScreen;
import ahud.adaptivehud.screens.elementscreen.SettingWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import static ahud.adaptivehud.screens.elementscreen.ElementScreen.errors;

public class CustomTextField extends TextFieldWidget {
    public boolean error;
    public int type;

    private ElementScreen screen;

    public CustomTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text, SettingWidget parent) {
        super(textRenderer, x, y, width, height, text);
        screen = (ElementScreen) parent.PARENT;
    }

    public void setError(boolean value, String errorMessage) {
        error = value;
        if (value) {
            if (!errors.containsKey(this.getMessage().getString())) {
                errors.put(this.getMessage().getString(), errorMessage);
                this.setEditableColor(0xa83832);
            }
        } else {
            errors.remove(this.getMessage().getString());
            this.setEditableColor(0xFFFFFF);
        }
        screen.updateDoneButton();
    }
}
