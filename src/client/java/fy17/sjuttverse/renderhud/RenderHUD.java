package fy17.sjuttverse.renderhud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fy17.sjuttverse.ConfigFiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Iterator;

import static fy17.sjuttverse.ConfigFiles.configFile;
import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class RenderHUD {
    public void renderCustomHud(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        VariableParser parser = new VariableParser();

        drawContext.getMatrices().push();
        Float scale = configFile.getAsJsonObject().get("default_size").getAsFloat();
        drawContext.getMatrices().scale(scale, scale, 1);

        for (JsonElement element : ConfigFiles.elementArray) {
            JsonObject x = element.getAsJsonObject();
            if (x.get("enabled").getAsBoolean()) {
                String parsedText = parser.parseVariable(x.get("value").getAsString());

                int paddingY = 0;
                int paddingX = 0;

                if (x.has("advanced")) {
                    drawContext.getMatrices().scale(0.5f, 0.5f, 1);
                }

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();

                    drawContext.fill(
                            x.get("posX").getAsInt(),
                            x.get("posY").getAsInt(),
                            x.get("posX").getAsInt() + client.textRenderer.getWidth(parsedText) + 2 * paddingX,
                            x.get("posY").getAsInt() + client.textRenderer.fontHeight + 2 * paddingY,
                            parseColor(x.get("background").getAsJsonObject().get("backgroundColor").getAsString())
                    );
                }

                drawContext.drawText(
                        client.textRenderer,
                        parsedText,
                        x.get("posX").getAsInt() + paddingX,
                        x.get("posY").getAsInt() + paddingY + 1,
                        parseColor(x.get("textColor").getAsString()),
                        x.get("shadow").getAsBoolean()
                );
                drawContext.getMatrices().pop();
            }
        }
    }

    public int parseColor(String colorString) {
        String[] rgba = colorString.split(",");
        try {
            return (
                    (((int) (Float.parseFloat(rgba[3]) * 255)) << 24) |
                            (Integer.parseInt(rgba[0]) << 16) |
                            (Integer.parseInt(rgba[1]) << 8) |
                            Integer.parseInt(rgba[2])
            );
        } catch (Exception e) {
            return 0;
        }
    }
}
