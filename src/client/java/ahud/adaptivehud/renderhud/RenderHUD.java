package ahud.adaptivehud.renderhud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ahud.adaptivehud.ConfigFiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import static ahud.adaptivehud.ConfigFiles.configFile;
import static ahud.adaptivehud.adaptivehud.LOGGER;

public class RenderHUD {
    public void renderCustomHud(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        VariableParser parser = new VariableParser();

        Float defaultScale = configFile.getAsJsonObject().get("default_size").getAsFloat();
        MatrixStack matrices = drawContext.getMatrices();

        matrices.push();
        matrices.scale(defaultScale, defaultScale, 1);

        for (JsonElement element : ConfigFiles.elementArray) {
            JsonObject x = element.getAsJsonObject();
            if (x.get("enabled").getAsBoolean()) {
                String parsedText = parser.parseVariable(x.get("value").getAsString());

                int paddingY = 0;
                int paddingX = 0;

                if (x.has("advanced")) {
                    Float decScale = x.get("advanced").getAsJsonObject().get("scale").getAsFloat();
                    matrices.push();
                    matrices.scale(decScale, decScale, 1);
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

                if (x.has("advanced")) {
                    matrices.pop();
                }
            }
        }
        matrices.pop();
    }

    public int parseColor(String colorString) {
        try {
            if (colorString.startsWith("#")) {
                colorString = colorString.substring(1);
            }

            int alpha = 255;
            if (colorString.length() == 8) {
                alpha = Integer.parseInt(colorString.substring(0, 2), 16);
                colorString = colorString.substring(2);
            }

            int red = Integer.parseInt(colorString.substring(0, 2), 16);
            int green = Integer.parseInt(colorString.substring(2, 4), 16);
            int blue = Integer.parseInt(colorString.substring(4, 6), 16);

            return (alpha << 24) | (red << 16) | (green << 8) | blue;
        } catch (Exception e) {
            return 0;
        }
    }
}
