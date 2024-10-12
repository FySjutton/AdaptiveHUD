package ahud.adaptivehud.screens.editscreen;

import ahud.adaptivehud.screens.elementscreen.SettingWidget;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

@Environment(EnvType.CLIENT)
public class EditScreen extends Screen {
    private final SettingWidget PARENT;
    private JsonObject element;

    public EditScreen(SettingWidget parent, JsonObject element) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = parent;
        this.element = element;
    }

    @Override
    protected void init() {
        EditorWidget editor = new EditorWidget(textRenderer, 0, 0, width / 2, height / 2, Text.of("hello"));
        editor.setText(element.get("value").getAsString());
        editor.setChangeListener(x -> {
            element.addProperty("value", editor.getText());

            // .replace("\n", "HELLO")
        });
        addDrawableChild(editor);
    }

    @Override
    public void close() {
        // parent.parent is ElementScreen.java
        client.setScreen(PARENT.PARENT);
    }
}
