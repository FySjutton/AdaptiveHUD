package avox.adaptivehud;

import avox.adaptivehud.anchor.AnchorMode;
import avox.adaptivehud.anchor.AnchorPointX;
import avox.adaptivehud.anchor.AnchorPointY;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;

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
        if (draggedElement != null) {
            context.fill(draggedElement.anchorPoint.getX(), draggedElement.anchorPoint.getY() - 1, draggedElement.x, draggedElement.anchorPoint.getY() + 1, 0xFFFFFFFF);
            context.fill(draggedElement.x, draggedElement.anchorPoint.getY(), draggedElement.x + 1, draggedElement.y, 0xFFFFFFFF);
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

            int windowWidth = client.getWindow().getScaledWidth();
            int windowHeight = client.getWindow().getScaledHeight();
            int elementWidth = draggedElement.getEstimatedWidth();
            int elementHeight = draggedElement.getEstimatedHeight();

            // Auto anchor based on screen position
            if (draggedElement.anchorPoint.mode.equals(AnchorMode.Auto)) {
                int centerX = draggedElement.x + elementWidth / 2;
                int centerY = draggedElement.y + elementHeight / 2;

                if (centerX < windowWidth / 3) {
                    draggedElement.anchorPoint.x = AnchorPointX.Left;
                } else if (centerX > windowWidth / 3 * 2) {
                    draggedElement.anchorPoint.x = AnchorPointX.Right;
                } else {
                    draggedElement.anchorPoint.x = AnchorPointX.Center;
                }

                if (centerY < windowHeight / 3) {
                    draggedElement.anchorPoint.y = AnchorPointY.Top;
                } else if (centerY > windowHeight / 3 * 2) {
                    draggedElement.anchorPoint.y = AnchorPointY.Bottom;
                } else {
                    draggedElement.anchorPoint.y = AnchorPointY.Middle;
                }
            }

//            // Auto origin based on grab position inside element
//            if (draggedElement.selfAlign.mode == SelfAlignMode.Auto) {
//                double localX = mouseX - draggedElement.x;
//                double localY = mouseY - draggedElement.y;
//
//                if (localX < (double) elementWidth / 3) {
//                    draggedElement.selfAlign.x = SelfAlignX.Left;
//                } else if (localX > (double) elementWidth / 3 * 2) {
//                    draggedElement.selfAlign.x = SelfAlignX.Right;
//                } else {
//                    draggedElement.selfAlign.x = SelfAlignX.Center;
//                }
//
//                if (localY < (double) elementHeight / 3) {
//                    draggedElement.selfAlign.y = SelfAlignY.Top;
//                } else if (localY > (double) elementHeight / 3 * 2) {
//                    draggedElement.selfAlign.y = SelfAlignY.Bottom;
//                } else {
//                    draggedElement.selfAlign.y = SelfAlignY.Middle;
//                }
//            }
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
