package avox.adaptivehud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.Widget;

import static avox.adaptivehud.AdaptiveHUD.LOGGER;

public class ScaleWidget implements Drawable, Element, Selectable {
    public boolean visible = false;
    public int x = 0;
    public int y = 0;
    public HudElement hudElement;

    private int originalWidth;
    private int originalHeight;


    private final int scaleButtonSize = 2;

    private DragCorner activeCorner = DragCorner.NONE;

    public void disableVisibility() {
        visible = false;
    }

    public void setVisible(HudElement hudElement, int x, int y) {
        this.x = x;
        this.y = y;
        this.hudElement = hudElement;
        originalWidth = hudElement.getEstimatedWidthWithoutScale();
        originalHeight = hudElement.getEstimatedHeightWithoutScale();
        visible = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (visible) {
            int elmWidth = hudElement.getEstimatedWidth();
            int elmHeight = hudElement.getEstimatedHeight();
//            LOGGER.info("render");
            context.fill(x, y, x + scaleButtonSize, y + scaleButtonSize, 0xFF4287f5);
            context.fill(x + elmWidth, y, x + elmWidth - scaleButtonSize, y + scaleButtonSize, 0xFF4287f5);
            context.fill(x + elmWidth, y + elmHeight, x + elmWidth - scaleButtonSize, y + elmHeight - scaleButtonSize, 0xFF4287f5);
            context.fill(x, y + elmHeight, x + scaleButtonSize, y + elmHeight - scaleButtonSize, 0xFF4287f5);
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        if (!visible) return false;

        LOGGER.info("changed");
        int elmWidth = hudElement.getEstimatedWidth();
        int elmHeight = hudElement.getEstimatedHeight();
        if (x < mouseX && mouseX < x + scaleButtonSize && y < mouseY && mouseY < y + scaleButtonSize) {
            activeCorner = DragCorner.TOP_LEFT;
        } else if (x + elmWidth - scaleButtonSize < mouseX && mouseX < x + elmWidth && y < mouseY && mouseY < y + scaleButtonSize) {
            activeCorner = DragCorner.TOP_RIGHT;
        } else if (x + elmWidth - scaleButtonSize < mouseX && mouseX < x + elmWidth && y + elmHeight - scaleButtonSize < mouseY && mouseY < y + elmHeight) {
            activeCorner = DragCorner.BOTTOM_RIGHT;
        } else if (x < mouseX && mouseX < x + scaleButtonSize && y + elmHeight - scaleButtonSize < mouseY && mouseY < y + elmHeight) {
            activeCorner = DragCorner.BOTTOM_LEFT;
        } else {
            activeCorner = DragCorner.NONE;
        }

        return activeCorner != DragCorner.NONE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (activeCorner == DragCorner.NONE) return false;

        switch (activeCorner) {
            case TOP_LEFT -> {
                hudElement.x = (int) mouseX;
                hudElement.y = (int) mouseY;

                double cursorDeltaX = Math.abs(mouseX - x);
                double cursorDeltaY = Math.abs(mouseY - y);

                double scaleX = cursorDeltaX / originalWidth;
                double scaleY = cursorDeltaY / originalHeight;

                double uniformScale = Math.max(0.4f, Math.min(scaleX, scaleY)); // 0.4 min scale

                hudElement.scale = (float) uniformScale;
            }
//            case TOP_RIGHT -> {
//                int newWidth = (int)(mouseX - x);
//                int newHeight = (int)(elmHeight + (y - mouseY));
//                y = (int)mouseY;
//                elmWidth = Math.max(1, newWidth);
//                elmHeight = Math.max(1, newHeight);
//            }
            case BOTTOM_RIGHT -> {
                LOGGER.info("here");
                double cursorDeltaX = mouseX - x;
                double cursorDeltaY = mouseY - y;

                double scaleX = cursorDeltaX / originalWidth;
                double scaleY = cursorDeltaY / originalHeight;

                double uniformScale = Math.max(0.4f, Math.min(scaleX, scaleY)); // 0.4 min scale

                hudElement.scale = (float) uniformScale;
                LOGGER.info(String.valueOf(hudElement.scale));
            }
//            case BOTTOM_LEFT -> {
//                int newWidth = (int)(elmWidth + (x - mouseX));
//                int newHeight = (int)(mouseY - y);
//                x = (int)mouseX;
//                elmWidth = Math.max(1, newWidth);
//                elmHeight = Math.max(1, newHeight);
//            }
        }

        return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.HOVERED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    private enum DragCorner {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
}
