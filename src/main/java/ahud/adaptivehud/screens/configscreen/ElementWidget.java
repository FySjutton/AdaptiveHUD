package ahud.adaptivehud.screens.configscreen;

import ahud.adaptivehud.ConfigFiles;
import ahud.adaptivehud.JsonValidator;
import ahud.adaptivehud.screens.elementscreen.ElementScreen;
import ahud.adaptivehud.screens.elementscreen.widgets.CustomButton;
import ahud.adaptivehud.screens.elementscreen.widgets.CustomTextField;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.elementArray;

public class ElementWidget extends ElementListWidget<ElementWidget.Entry> {
    private final ConfigScreen PARENT;

    private final String ON_TEXT = Text.translatable("adaptivehud.config.button.on").getString();
    private final String OFF_TEXT = Text.translatable("adaptivehud.config.button.off").getString();

    private final int screenWidth;

    public ElementWidget(Screen parent, int width, int height) {
        super(MinecraftClient.getInstance(), width / 2 - 17, height - 59, 52, 25);
        this.setX(width / 2); // super doesn't take x?

        this.PARENT = (ConfigScreen) parent;
        this.screenWidth = width;

        updateElementList(null);
    }

    protected void updateElementList(String search) {
        clearEntries();

        for (JsonElement element : elementArray) {
            String name = element.getAsJsonObject().get("name").getAsString();
            if (search != null) {
                if (!name.contains(search)) {
                    continue;
                }
            }
            addEntry(new Entry(element.getAsJsonObject(), PARENT));
        }
    }

    @Override
    protected int getScrollbarX() {
        return screenWidth - 15;
    }

    @Override
    public int getRowWidth() {
        return screenWidth - 15;
    }

    public class Entry extends ElementListWidget.Entry<ElementWidget.Entry> {
        public ButtonWidget editBtn;
        public ButtonWidget toggleBtn;
        public ButtonWidget deleteBtn;

        public Entry(JsonObject element, ConfigScreen parent) {
            ButtonWidget editButton = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> editElement(element))
                    .dimensions(screenWidth / 2 + 5, 0, (screenWidth - 64) / 4, 20)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.editElement")))
                    .build();
            ButtonWidget toggleButton = ButtonWidget.builder(Text.literal((element.getAsJsonObject().get("enabled").getAsBoolean() ? ON_TEXT : OFF_TEXT)), btn -> switchEnabled(btn, element))
                    .dimensions((3 * screenWidth) / 4 - 9, 0, screenWidth / 8 - 8, 20)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.toggleElement")))
                    .build();
            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("\uD83D\uDDD1"), btn -> parent.deleteElement(element))
                    .dimensions((7 * screenWidth) / 8 - 14, 0, screenWidth / 8 - 8, 20)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.deleteElement")))
                    .build();
            editBtn = editButton;
            toggleBtn = toggleButton;
            deleteBtn = deleteButton;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            List<Selectable> children = new ArrayList<>();
            children.add(editBtn);
            children.add(toggleBtn);
            children.add(deleteBtn);
            return children;
        }

        @Override
        public List<? extends net.minecraft.client.gui.Element> children() {
            List<Element> children = new ArrayList<>();
            children.add(editBtn);
            children.add(toggleBtn);
            children.add(deleteBtn);
            return children;
        }

        @Override
        public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.editBtn.setY(y);
            this.toggleBtn.setY(y);
            this.deleteBtn.setY(y);

            this.editBtn.render(drawContext, mouseX, mouseY, tickDelta);
            this.toggleBtn.render(drawContext, mouseX, mouseY, tickDelta);
            this.deleteBtn.render(drawContext, mouseX, mouseY, tickDelta);
        }
    }

    private void editElement(JsonElement element) {
        MinecraftClient.getInstance().setScreen(new ElementScreen(PARENT, element.getAsJsonObject()));
    }

    private void switchEnabled(ButtonWidget button, JsonElement element) {
        JsonObject new_object = element.getAsJsonObject();

        boolean current = new_object.get("enabled").getAsBoolean();
        button.setMessage(Text.of(current ? OFF_TEXT : ON_TEXT));
        new_object.addProperty("enabled", !current);

        elementArray.set(elementArray.indexOf(element), new_object);
        PARENT.changesMade();
    }
}
