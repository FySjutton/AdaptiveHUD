package ahud.adaptivehud.screens.elementscreen.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import static ahud.adaptivehud.screens.elementscreen.SettingWidget.errors;

public class CustomTextField extends TextFieldWidget {
    public boolean error;
    public int type;
    public String errorText;

    public CustomTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public void setError(boolean value, String errorMessage) {
        error = value;
        if (value) {
            if (!errors.contains(this.getMessage().getString())) {
                errorText = errorMessage;
                errors.add(this.getMessage().getString());
                this.setEditableColor(0xa83832);
            }
        } else {
            errors.remove(this.getMessage().getString());
            this.setEditableColor(0xFFFFFF);
        }
    }
}
