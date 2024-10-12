package ahud.adaptivehud.screens.editscreen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Iterator;
import java.util.Objects;

public class EditorWidget extends EditBoxWidget {
    public EditorWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text message) {
        super(textRenderer, x, y, width, height, Text.empty(), message);
    }

}
