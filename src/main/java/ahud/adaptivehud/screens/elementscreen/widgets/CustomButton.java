package ahud.adaptivehud.screens.elementscreen.widgets;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CustomButton extends ButtonWidget {
    public int type;

    protected CustomButton(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    // Add a builder method that returns CustomButton
    public static CustomButtonBuilder customBuilder(Text message, PressAction onPress) {
        return new CustomButtonBuilder(message, onPress);
    }

    // Custom builder class for CustomButton
    public static class CustomButtonBuilder {
        private int x, y, width, height;
        private final Text message;
        private final PressAction onPress;

        public CustomButtonBuilder(Text message, PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public CustomButtonBuilder dimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public CustomButton build() {
            return new CustomButton(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }
}