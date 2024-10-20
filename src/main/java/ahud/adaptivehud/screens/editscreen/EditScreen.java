package ahud.adaptivehud.screens.editscreen;

import ahud.adaptivehud.screens.elementscreen.SettingWidget;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
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
    private TextFieldWidget search;

    public boolean showVariableBox = false;

    public EditScreen(SettingWidget parent, JsonObject element) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = parent;
        this.element = element;
    }

    @Override
    protected void init() {
        EditorWidget editor = new EditorWidget(textRenderer, width / 2 - 10, 10, width / 2, height - 20, Text.of("hello"));
        editor.setText(element.get("value").getAsString());
        editor.setChangeListener(x -> {
            element.addProperty("value", editor.getText());
        });
        addDrawableChild(editor);

        ButtonWidget insertbtn = ButtonWidget.builder(Text.of("Insert Variable"), btn -> insertVariable()).dimensions(10, 10, 100, 25).build();
        addDrawableChild(insertbtn);

        TextFieldWidget search = new TextFieldWidget(textRenderer, 8, height - 34, 100, 20, Text.of("test"));
        this.search = search;
//        addDrawableChild(search);
        addSelectableChild(search);
        search.setChangedListener(newValue -> {
            this.suggestor.updateSuggestions();
        });

        suggestor = new Suggestor(search, textRenderer, 8, height - 34, 90);
        updateVarVisibility(false);
    }

    private void updateVarVisibility(boolean visible) {
        showVariableBox = !showVariableBox;
        this.suggestor.visible = showVariableBox;
        this.search.visible = showVariableBox;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
//        context.enableScissor(5, height - 80, width / 2 - 10 - 5, height - 10);
        context.drawBorder(4, height - 82, width / 2 - 10 - 8, 72, 0xFFA0A0A0);
//        context.fill(4, height - 81, width / 2 - 10 - 4, height - 9, 0xFFFFFFFF);
//        context.disableScissor();
        context.fill(5, height - 81, width / 2 - 10 - 5, height - 11, 0xad000000);

        if (showVariableBox) {
            MatrixStack matrices = context.getMatrices();

            matrices.push();
            matrices.translate(0, 0, -1);
            context.drawTextWithShadow(textRenderer, "Insert Variable:", 8, height - 78, 0xFFFFFFFF);
            matrices.pop();
        }
        this.search.render(context, mouseX, mouseY, delta);
        if (search.isFocused()) {
            this.suggestor.render(context, mouseX, mouseY);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.suggestor.mouseClicked();
        if (this.hoveredElement(mouseX, mouseY).isEmpty()) {
            this.setFocused(null);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.suggestor.keyPressed(keyCode)) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.suggestor.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        verticalAmount = MathHelper.clamp(verticalAmount, -1.0, 1.0);
        this.suggestor.mouseScrolled(verticalAmount > 0, mouseX, mouseY);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        client.setScreen(PARENT.PARENT);
    }

    private void insertVariable() {
        LOGGER.info("insert variable");
        updateVarVisibility(!search.visible);

//        for (String name : VARIABLES.keySet()) {
//            LOGGER.info(name);
//            Class<?> varType = VARIABLES.get(name).getReturnType();
//            Class<?> attributeClass = ATTRIBUTE_CLASSES.get(varType);
//            if (attributeClass != null) {
//                Method[] methods = attributeClass.getDeclaredMethods();
//                for (Method method : methods) {
//                    LOGGER.info(name + "." + method.getName());
//                }
//            }
//        }
    }
}
