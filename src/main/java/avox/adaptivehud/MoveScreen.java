package avox.adaptivehud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Optional;

public class MoveScreen extends Screen {
    private final ArrayList<HudElement> hudElements;
    private HudElement draggedElement;
    private double relativeX;
    private double relativeY;

    public MoveScreen(Text title) {
        super(title);

        hudElements = ElementManager.getHudElements();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        for (HudElement hudElement : hudElements) {
            context.fill(hudElement.x, hudElement.y, hudElement.x + hudElement.getEstimatedWidth(), hudElement.y + hudElement.getEstimatedHeight(), 0xFF000000);
            context.drawCenteredTextWithShadow(client.textRenderer, hudElement.name, (hudElement.x + hudElement.getEstimatedWidth() / 2), hudElement.y + (hudElement.getEstimatedHeight() / 2) - client.textRenderer.fontHeight / 2, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (HudElement hudElement : hudElements) {
            if (mouseX > hudElement.x && mouseX < hudElement.x + hudElement.getEstimatedWidth() && mouseY > hudElement.y && mouseY < hudElement.y + hudElement.getEstimatedHeight()) {
                draggedElement = hudElement;
                relativeX = hudElement.x - mouseX;
                relativeY = hudElement.y - mouseY;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggedElement != null) {
            draggedElement.x = Math.clamp((int) (mouseX + relativeX), 0, client.getWindow().getScaledWidth() - draggedElement.getEstimatedWidth());
            draggedElement.y = Math.clamp((int) (mouseY + relativeY), 0, client.getWindow().getScaledHeight() - draggedElement.getEstimatedHeight());
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggedElement = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // DRA SOM DU VILL; SEDAN RÄKNA UT RELATIVE COORDS NÄR ALLT ÄR KLART
}
