package avox.adaptivehud;

import avox.adaptivehud.anchor.AnchorMode;
import avox.adaptivehud.anchor.AnchorPoint;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class TextElement extends HudElement {
    private final MinecraftClient client;
    public String content;

    public TextElement(JsonObject savedData, AnchorPoint anchorPoint, String name) {
        super(savedData, anchorPoint, name);
        client = MinecraftClient.getInstance();
        content = "Testing wehoo!";
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
