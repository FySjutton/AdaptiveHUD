package ahud.adaptivehud.screens.configscreen;

import ahud.adaptivehud.screens.elementscreen.ElementScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
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
        super(MinecraftClient.getInstance(), width / 2 - 17, height - 59, 52, 20);
        this.setX(width / 2); // super doesn't take x?

        this.PARENT = (ConfigScreen) parent;
        this.screenWidth = width;

        updateElementList(null);
    }

    public void updateElementList(String search) {
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
            ButtonWidget toggleButton = ButtonWidget.builder(Text.literal((element.getAsJsonObject().get("enabled").getAsBoolean() ? "☑" : "☐")), btn -> switchEnabled(btn, element))
                    .dimensions(screenWidth / 2 + 5, 0, 18, 18)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.toggleElement")))
                    .build();
            ButtonWidget editButton = ButtonWidget.builder(Text.literal(element.getAsJsonObject().get("name").getAsString()), btn -> editElement(element))
                    .dimensions(screenWidth / 2 + 5 + 18 + 2, 0, (screenWidth - 17 - 20 - 5 - 2) - (screenWidth / 2 + 5 + 18 + 2) , 18)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.editElement")))
                    .build();

            ButtonWidget deleteButton = ButtonWidget.builder(Text.literal("\uD83D\uDDD1").withColor(0xffff7c75), btn -> parent.deleteElement(element))
                    .dimensions(screenWidth - 17 - 20 - 5, 0, 20, 18)
                    .tooltip(Tooltip.of(Text.translatable("adaptivehud.config.deleteElement")))
                    .build();
            toggleBtn = toggleButton;
            editBtn = editButton;
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
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.editBtn.setY(y);
            this.toggleBtn.setY(y);
            this.deleteBtn.setY(y);

            this.editBtn.render(context, mouseX, mouseY, tickDelta);
            this.toggleBtn.render(context, mouseX, mouseY, tickDelta);
            this.deleteBtn.render(context, mouseX, mouseY, tickDelta);
        }
    }

    private void editElement(JsonElement element) {
        MinecraftClient.getInstance().setScreen(new ElementScreen(PARENT, element.getAsJsonObject()));
    }

    private void switchEnabled(ButtonWidget button, JsonElement element) {
        JsonObject new_object = element.getAsJsonObject();

        boolean current = new_object.get("enabled").getAsBoolean();
        button.setMessage(Text.of(current ? "☐" : "☑"));
        new_object.addProperty("enabled", !current);

        elementArray.set(elementArray.indexOf(element), new_object);
        PARENT.changesMade();
    }
}
