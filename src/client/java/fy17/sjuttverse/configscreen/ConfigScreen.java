package fy17.sjuttverse.configscreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private List<String> stringList;
    private List<ButtonWidget> buttonList;

    public ConfigScreen() {
        super(Text.literal("Sjuttverse"));
    }

    @Override
    protected void init() {
        ScrollableList scrollableList = new ScrollableList(height, width);
        addDrawableChild(scrollableList);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Sjuttverse - Elements"), width / 2, 20, 0xffffff);
    }
}
