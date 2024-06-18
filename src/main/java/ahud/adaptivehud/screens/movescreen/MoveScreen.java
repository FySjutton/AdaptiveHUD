package ahud.adaptivehud.screens.movescreen;

import ahud.adaptivehud.renderhud.RenderHUD;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;


@Environment(EnvType.CLIENT)
public class MoveScreen extends Screen {
    private final Screen parent;
    private final List<Object[]> posList;

    private double offsetX;
    private double offsetY;
    private double height;
    private double width;
    private Object[] dragInf;
    private JsonObject dragged;


    public MoveScreen(Screen parent) {
        super(Text.translatable("adaptivehud.config.title"));
        this.parent = parent;
        this.posList = new RenderHUD().generatePositions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);
        new RenderHUD().renderCustomHud(context, 0);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragged != null) {
            dragged.addProperty("posX", mouseX - offsetX);
            dragged.addProperty("posY", mouseY - offsetY);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (Object[] x : posList) {
                if ((mouseX >= (double) x[1] && mouseX <= (double) x[3]) && (mouseY >= (double) x[2] && mouseY <= (double) x[4])) {
                    dragged = ((JsonElement) x[0]).getAsJsonObject();
                    offsetX = mouseX - (double) x[1];
                    offsetY = mouseY - (double) x[2];
                    height = (double) x[4] - (double) x[2];
                    width = (double) x[3] - (double) x[1];
                    dragInf = x;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragged != null) {
            dragInf[1] = dragged.get("posX").getAsDouble();
            dragInf[2] = dragged.get("posY").getAsDouble();
            dragInf[3] = dragged.get("posX").getAsDouble() + width;
            dragInf[4] = dragged.get("posY").getAsDouble() + height;
            dragged = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}