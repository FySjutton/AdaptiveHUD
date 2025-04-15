package avox.adaptivehud;

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
            Position screenOrigin = draggedElement.alignment.getScreenOrigin();
            Position elementOrigin = draggedElement.alignment.getElementOrigin();
            context.fill(screenOrigin.x(), screenOrigin.y(), elementOrigin.x(), screenOrigin.y() + 1, 0xFFFFFFFF);
            context.fill(elementOrigin.x(), screenOrigin.y(), elementOrigin.x() + 1, elementOrigin.y(), 0xFFFFFFFF);
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
            if (draggedElement.alignment.anchorPoint.mode.equals(Alignment.AnchorPoint.Mode.AUTO)) {
                int centerX = draggedElement.x + elementWidth / 2;
                int centerY = draggedElement.y + elementHeight / 2;

                if (centerX < windowWidth / 3) {
                    draggedElement.alignment.anchorPoint.x = Alignment.X.LEFT;
                } else if (centerX > windowWidth / 3 * 2) {
                    draggedElement.alignment.anchorPoint.x = Alignment.X.RIGHT;
                } else {
                    draggedElement.alignment.anchorPoint.x = Alignment.X.CENTER;
                }

                if (centerY < windowHeight / 3) {
                    draggedElement.alignment.anchorPoint.y = Alignment.Y.TOP;
                } else if (centerY > windowHeight / 3 * 2) {
                    draggedElement.alignment.anchorPoint.y = Alignment.Y.BOTTOM;
                } else {
                    draggedElement.alignment.anchorPoint.y = Alignment.Y.MIDDLE;
                }
            }

            if (draggedElement.alignment.selfAlign.mode.equals(Alignment.SelfAlign.Mode.AUTO)) {
                int centerX = draggedElement.x + elementWidth / 2;
                int centerY = draggedElement.y + elementHeight / 2;

                if (centerX < windowWidth / 3) {
                    draggedElement.alignment.selfAlign.x = Alignment.X.LEFT;
                } else if (centerX > windowWidth / 3 * 2) {
                    draggedElement.alignment.selfAlign.x = Alignment.X.RIGHT;
                } else {
                    draggedElement.alignment.selfAlign.x = Alignment.X.CENTER;
                }

                if (centerY < windowHeight / 3) {
                    draggedElement.alignment.selfAlign.y = Alignment.Y.TOP;
                } else if (centerY > windowHeight / 3 * 2) {
                    draggedElement.alignment.selfAlign.y = Alignment.Y.BOTTOM;
                } else {
                    draggedElement.alignment.selfAlign.y = Alignment.Y.MIDDLE;
                }
            }

            Position relativeCord = draggedElement.alignment.getRelativeCords(
                    new Position(draggedElement.x, draggedElement.y),
                    new Position(draggedElement.getEstimatedWidth(), draggedElement.getEstimatedHeight())
            );
            draggedElement.relativeX = relativeCord.x();
            draggedElement.relativeY = relativeCord.y();
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
