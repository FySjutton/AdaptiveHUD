package ahud.adaptivehud.screens.editscreen;

import ahud.adaptivehud.screens.elementscreen.SettingWidget;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.lang.reflect.Method;
import java.util.List;

import static ahud.adaptivehud.AdaptiveHUD.*;

@Environment(EnvType.CLIENT)
public class EditScreen extends Screen {
    private final SettingWidget PARENT;
    private JsonObject element;
    private Suggestor suggestor;

    public EditScreen(SettingWidget parent, JsonObject element) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = parent;
        this.element = element;
    }

    @Override
    protected void init() {
        EditorWidget editor = new EditorWidget(textRenderer, 0, 0, width / 2, height / 2, Text.of("hello"));
        editor.setText(element.get("value").getAsString());
        editor.setChangeListener(x -> {
            element.addProperty("value", editor.getText());
        });
        addDrawableChild(editor);

        ButtonWidget insertbtn = ButtonWidget.builder(Text.of("Test"), btn -> insertVariable()).dimensions(200, 100, 100, 50).build();
        addDrawableChild(insertbtn);

        TextFieldWidget search = new TextFieldWidget(textRenderer, width / 2, height / 2, 100, 20, Text.of("test"));
        addDrawableChild(search);
        search.setChangedListener(newValue -> {
            this.suggestor.refresh();
            this.suggestor.setWindowActive(true);
        });

        suggestor = new Suggestor(client, this, search, textRenderer, 1, 5, true, -805306368);

//        ChatInputSuggestor suggestor = new ChatInputSuggestor(client, this, search, textRenderer, true, true, 0, 20, true, 0xFFFFFFFF).SuggestionWindow();
////        addDrawableChild(suggestor);
//        new ChatInputSuggestor.SuggestionWindow(0, 0, List.of(), false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.suggestor.render(context, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.suggestor.keyPressed(keyCode)) {
            return true;
        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        verticalAmount = MathHelper.clamp(verticalAmount, -1.0, 1.0);
        if (this.suggestor.mouseScrolled(verticalAmount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.suggestor.mouseClicked((int) mouseX, (int) mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        // parent.parent is ElementScreen.java
        client.setScreen(PARENT.PARENT);
    }

    private void insertVariable() {
        LOGGER.info("insert variable");

        for (String name : VARIABLES.keySet()) {
            LOGGER.info(name);
//            Class<?> varType = VARIABLES.get(name).getReturnType();
//            Class<?> attributeClass = ATTRIBUTE_CLASSES.get(varType);
//            if (attributeClass != null) {
//                Method[] methods = attributeClass.getDeclaredMethods();
//                for (Method method : methods) {
//                    LOGGER.info(name + "." + method.getName());
//                }
//            }
        }
    }
}
