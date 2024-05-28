package fy17.sjuttverse.configscreen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;

public class ScrollableList extends ScrollableWidget {
    private final List<String> stringList;
    private final List<ButtonWidget> buttonList;
    private final int buttonHeight;
    private final int buttonMargin;

    public ScrollableList(int x, int y, int width, int height, List<String> stringList, List<ButtonWidget> buttonList, int buttonHeight, int buttonMargin) {
        super(x, y, width, height, Text.literal("Elements"));
        this.stringList = stringList;
        this.buttonList = buttonList;
        this.buttonHeight = buttonHeight;
        this.buttonMargin = buttonMargin;
    }

    @Override
    protected int getContentsHeight() {
        return stringList.size() * (buttonHeight + buttonMargin);
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    public void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        double adjustedMouseY = mouseY + getScrollY();
        int startY = this.getY() + (int) getScrollY();
        int endY = startY + this.height;

        for (ButtonWidget buttonWidget : buttonList) {
            int buttonTop = buttonWidget.getY();
            int buttonBottom = buttonTop + buttonHeight;

            buttonWidget.render(context, mouseX, (int) adjustedMouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double adjustedMouseY = mouseY + getScrollY();
        for (ButtonWidget buttonWidget : buttonList) {
            if (buttonWidget.isMouseOver(mouseX, adjustedMouseY)) {
                buttonWidget.onClick(mouseX, adjustedMouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
