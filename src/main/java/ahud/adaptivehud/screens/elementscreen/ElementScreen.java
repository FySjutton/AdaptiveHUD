package ahud.adaptivehud.screens.elementscreen;

import ahud.adaptivehud.JsonValidator;
import ahud.adaptivehud.screens.configscreen.ConfigScreen;
import ahud.adaptivehud.Tools;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;
import static ahud.adaptivehud.ConfigFiles.elementArray;

@Environment(EnvType.CLIENT)
public class ElementScreen extends Screen {
    public final Screen PARENT;
    public static HashMap<String, String> errors = new HashMap<>();

    private ButtonWidget doneButton;
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private JsonObject element;
    private JsonObject backupElement;

    public ElementScreen(Screen PARENT, JsonObject element) {
        super(Text.of("AdaptiveHUD"));
        this.PARENT = PARENT;
        this.element = element;
        this.backupElement = element.deepCopy();
    }

    @Override
    public void init() {
        ButtonWidget doneBtn = ButtonWidget.builder(Text.translatable("adaptivehud.config.done"), btn -> close()).dimensions(width / 4, height - 25, width / 2, 20).build();
        this.addDrawableChild(doneBtn);
        this.doneButton = doneBtn;

        Tab[] tabs = new Tab[3];
        // Types:
        // 1: Boolean Button
        // 2: Text Field (no requirement)
        // 3: Text Field (name validation)
        // 4: Text Field (color validation)
        // 5: Text Field (int validation)
        // 6: Text Field (max width 1000)
        // 7: Button (left - center - right)
        // 8: Button (top - center - bottom)
        tabs[0] = new newTab(
            this, "general", null,
            new ArrayList<>(List.of("enabled", "name", "value", "textColor", "posX", "posY", "shadow")),
            new ArrayList<>(List.of(1, 2, 6, 4, 5, 5, 1))
        );
        tabs[1] = new newTab(
            this, "background", "background",
            new ArrayList<>(List.of("enabled", "paddingX", "paddingY", "backgroundColor")),
            new ArrayList<>(List.of(1, 5, 5, 4))
        );
        tabs[2] = new newTab(
                this, "alignment", "alignment",
                new ArrayList<>(List.of("itemAlignX", "itemAlignY", "selfAlignX", "selfAlignY", "textAlign")),
                new ArrayList<>(List.of(7, 8, 7, 8, 7))
        );

        TabNavigationWidget tabNavigation = TabNavigationWidget.builder(this.tabManager, this.width).tabs(tabs).build();
        this.addDrawableChild(tabNavigation);

        tabNavigation.selectTab(0, false);
        tabNavigation.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return errors.isEmpty();
    }

    @Override
    public void close() {
        client.setScreen(PARENT);
    }

    private class newTab extends GridScreenTab {
        public SettingWidget settingWidget;
        public newTab(Screen parent, String tabName, String tabInd, ArrayList<String> settings, ArrayList<Integer> types) {
            super(Text.translatable("adaptivehud.config.title." + tabName));
            GridWidget.Adder adder = grid.createAdder(1);

            settingWidget = new SettingWidget(parent, tabInd, settings, types, element, width, height);
            adder.add(settingWidget);
        }
    }

    public void updateDoneButton() {
        doneButton.active = errors.isEmpty();
        if (!errors.isEmpty()) {
            doneButton.setMessage(Text.translatable("adaptivehud.config.save_error").append(Text.literal(errors.values().toArray()[0].toString())).withColor(0xFF4F4F));


        } else {
            doneButton.setMessage(Text.translatable("adaptivehud.config.done").withColor(0xFFFFFFFF));
        }
    }
}