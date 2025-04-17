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
    public int x;
    public int y;
    public int elmWidth;
    public int elmHeight;

    public void disableVisibility() {
        visible = false;
    }

    public void setVisible(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.elmWidth = width;
        this.elmHeight = height;
        visible = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (visible) {
//            LOGGER.info("render");
            context.fill(x, y, x + 2, y + 2, 0xFF4287f5);
            context.fill(x + elmWidth, y, x + elmWidth - 2, y + 2, 0xFF4287f5);
            context.fill(x + elmWidth, y + elmHeight, x + elmWidth - 2, y + elmHeight - 2, 0xFF4287f5);
            context.fill(x, y + elmHeight, x + 2, y + elmHeight - 2, 0xFF4287f5);
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        boolean hovered = false;
        if (visible) {
            if (x < mouseX && x + 2 > mouseX && y < mouseY && y + 2 > mouseY) {
                hovered = true;
            }
            if (x + elmWidth < mouseX && x + elmWidth - 2 > mouseX && y < mouseY && y + 2 > mouseY) {
                hovered = true;
            }
            if (x + elmWidth < mouseX && x + elmWidth - 2 > mouseX && y + elmHeight < mouseY && y + elmHeight - 2 > mouseY) {
                hovered = true;
            }
            if (x < mouseX && x + 2 > mouseX && y + elmHeight < mouseY && y + elmHeight - 2 > mouseY) {
                hovered = true;
            }
        }
        return hovered;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        LOGGER.info("yes");
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
}
