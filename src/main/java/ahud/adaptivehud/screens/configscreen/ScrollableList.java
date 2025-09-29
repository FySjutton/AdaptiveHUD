package ahud.adaptivehud.screens.configscreen;

import ahud.adaptivehud.screens.elementscreen.ElementScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.elementArray;

public class ScrollableList extends ElementListWidget<ScrollableList.Entry> {
    private final ConfigScreen PARENT;
    private final String ON_TEXT = Text.translatable("adaptivehud.config.button.on").getString();
    private final String OFF_TEXT = Text.translatable("adaptivehud.config.button.off").getString();
    private int width;


    public ScrollableList(MinecraftClient client, int height, int width, ConfigScreen parent) {
        super(client, width / 2 - 10, height - 50 - 10, 50, 25);
        setX(width / 2);
        this.PARENT = parent;
        this.width = width;
        updateEntries();
    }

    public void updateEntries() {
        clearEntries();

        for (JsonElement element : elementArray) {
            addEntry(new Entry(element));
        }
    }

    @Override
    protected int getScrollbarX() {
        return width - 8;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 10;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    public class Entry extends ElementListWidget.Entry<Entry> {
        private ButtonWidget editBtn;
        private ButtonWidget enableBtn;
        private ButtonWidget deleteBtn;

        public Entry(JsonElement element) {
            int boxWidth = width / 2 - 10;
            int boxPosX = width / 2;

            editBtn = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> editElement(element))
                    .dimensions(boxPosX + 5, 0, (int) (0.6 * boxWidth - 12), 20)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.editElement")))
                    .build();
            enableBtn = ButtonWidget.builder(Text.literal((element.getAsJsonObject().get("enabled").getAsBoolean() ? ON_TEXT : OFF_TEXT)), btn -> switchEnabled(btn, element))
                    .dimensions((int) (boxPosX + 0.6 * boxWidth - 2), 0, (int) (0.25 * boxWidth - 5), 20)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.toggleElement")))
                    .build();
            deleteBtn = ButtonWidget.builder(Text.literal("\uD83D\uDDD1"), btn -> PARENT.deleteElement(element))
                    .dimensions((int) (0.6 * boxPosX + 1.25 * boxWidth + 2), 0, (int) (0.15 * boxWidth - 3), 20)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.deleteElement")))
                    .build();
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(editBtn, enableBtn, deleteBtn);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(editBtn, enableBtn, deleteBtn);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            editBtn.setY(getY());
            editBtn.render(context, mouseX, mouseY, deltaTicks);

            enableBtn.setY(getY());
            enableBtn.render(context, mouseX, mouseY, deltaTicks);

            deleteBtn.setY(getY());
            deleteBtn.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    private void editElement(JsonElement element) {
        MinecraftClient.getInstance().setScreen(new ElementScreen(PARENT, element));
    }

    private void switchEnabled(ButtonWidget button, JsonElement element) {
        JsonObject new_object = element.getAsJsonObject();
        if (button.getMessage().getString().equals(ON_TEXT)) {
            button.setMessage(Text.of(OFF_TEXT));
            new_object.addProperty("enabled", false);
        } else {
            button.setMessage(Text.of(ON_TEXT));
            new_object.addProperty("enabled", true);
        }
        elementArray.set(elementArray.indexOf(element), new_object);
        PARENT.changesMade();
    }
}