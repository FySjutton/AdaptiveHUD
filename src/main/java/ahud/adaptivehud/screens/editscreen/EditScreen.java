package ahud.adaptivehud.screens.editscreen;

import ahud.adaptivehud.screens.elementscreen.SettingWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class EditScreen extends Screen {
    private final SettingWidget PARENT;

    public EditScreen(SettingWidget parent) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = parent;


    }

    @Override
    protected void init() {
        addDrawableChild(new EditorWidget(textRenderer, 0, 0, width / 2, height / 2, Text.of("what")));
    }

    @Override
    public void close() {
        // parent.parent is ElementScreen.java
        client.setScreen(PARENT.PARENT);
    }
}
