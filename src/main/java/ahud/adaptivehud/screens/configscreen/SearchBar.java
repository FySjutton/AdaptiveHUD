package ahud.adaptivehud.screens.configscreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Timer;
import java.util.TimerTask;

public class SearchBar extends TextFieldWidget {
    private Timer timer;
    private static final long DELAY = 800;

    protected SearchBar(TextRenderer textRenderer, int width, ElementWidget elementWidget) {
        super(textRenderer, width / 2 + 17, 32, width / 2 - 10 - 17 - 7, 15, Text.of("Search Bar"));
        super.setChangedListener(text -> {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MinecraftClient.getInstance().execute(() -> {
                        elementWidget.updateElementList(text);
                    });
                    timer.cancel();
                }
            }, DELAY);
        });
    }
}
