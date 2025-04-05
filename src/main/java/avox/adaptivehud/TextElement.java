package avox.adaptivehud;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class TextElement extends HudElement {
    private final MinecraftClient client;
    public String content;

    public TextElement(JsonObject savedData, AnchorPoint anchorPoint) {
        super(savedData, anchorPoint);
        client = MinecraftClient.getInstance();
    }

    @Override
    public int getEstimatedWidth() {
        // unparsed
        return client.textRenderer.getWidth(content);
    }

    @Override
    public int getEstimatedHeight() {
        return 20;
    }

    @Override
    public void render(DrawContext context) {

    }

    @Override
    public JsonObject elementToSave() {
        return null;
    }
}
