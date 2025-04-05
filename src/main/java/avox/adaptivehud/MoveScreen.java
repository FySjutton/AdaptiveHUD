package avox.adaptivehud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class MoveScreen extends Screen {
    private ArrayList<HudElement> hudElements;
    public MoveScreen(Text title) {
        super(title);

        hudElements = ElementManager.getHudElements();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        for (HudElement hudElement : hudElements) {
            context.fill(hudElement.x, hudElement.y, hudElement.x + hudElement.getEstimatedWidth(), hudElement.y + hudElement.getEstimatedHeight(), 0);
            context.drawCenteredTextWithShadow(client.textRenderer, hudElement.name, (hudElement.x + hudElement.getEstimatedWidth()) / 2, (hudElement.y + hudElement.getEstimatedHeight()) / 2, 0xFFFFFFFF);
        }
    }

    // DRA SOM DU VILL; SEDAN RÄKNA UT RELATIVE COORDS NÄR ALLT ÄR KLART
}
