package ahud.adaptivehud.renderhud;

import ahud.adaptivehud.renderhud.variables.ValueParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ahud.adaptivehud.ConfigFiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.ConfigFiles.configFile;

public class RenderHUD {
    private boolean useLong;

    public RenderHUD(boolean useLong) {
        this.useLong = useLong;
    }

    public void renderCustomHud(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ValueParser parser = new ValueParser();

        MatrixStack matrices = drawContext.getMatrices();

        for (JsonElement element : ConfigFiles.elementArray) {
            JsonObject x = element.getAsJsonObject();
            if (x.get("enabled").getAsBoolean()) {
                float defaultScale;
                String parsedText;

                if (this.useLong) {
                    parsedText = parser.parseValue(x.get("value").getAsString());
                } else {
                    parsedText = x.get("name").getAsString();
                }

                int paddingY = 0;
                int paddingX = 0;
                int posX;
                int posY;

                matrices.push();
                float setScale = x.get("advanced").getAsJsonObject().get("scale").getAsFloat();
                if (setScale != 0) {
                    defaultScale = setScale;
                    matrices.scale(defaultScale, defaultScale, 1);
                } else {
                    defaultScale = configFile.getAsJsonObject().get("default_size").getAsFloat();
                    matrices.scale(defaultScale, defaultScale, 1);
                }

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();

                    posX = new coordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), client.textRenderer.getWidth(parsedText) + paddingX * 2, defaultScale, "X");
                    posY = new coordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), 9 + 1 + paddingY * 2, defaultScale, "Y");

                    drawContext.fill(
                            posX,
                            posY,
                            posX + client.textRenderer.getWidth(parsedText) + 2 * paddingX,
                            posY + 9 + 1 + 2 * paddingY,
                            parseColor(x.get("background").getAsJsonObject().get("backgroundColor").getAsString())
                    );
                } else {
                    posX = new coordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), client.textRenderer.getWidth(parsedText), defaultScale,"X");
                    posY = new coordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), 9 + 1, defaultScale,"Y");
                }

                drawContext.drawText(
                        client.textRenderer,
                        parsedText,
                        posX + paddingX,
                        posY + paddingY + 1,
                        parseColor(x.get("textColor").getAsString()),
                        x.get("shadow").getAsBoolean()
                );

                matrices.pop();
            }
        }
    }

    public int parseColor(String colorString) {
        try {
            if (colorString.startsWith("#")) {
                colorString = colorString.substring(1);
            }

            int alpha = 255;
            if (colorString.length() == 8) {
                alpha = Integer.parseInt(colorString.substring(6, 8), 16);
                colorString = colorString.substring(0, 6);
            }

            int red = Integer.parseInt(colorString.substring(0, 2), 16);
            int green = Integer.parseInt(colorString.substring(2, 4), 16);
            int blue = Integer.parseInt(colorString.substring(4, 6), 16);

            return (alpha << 24) | (red << 16) | (green << 8) | blue;
        } catch (Exception e) {
            return 0;
        }
    }



    public List<Object[]> generatePositions() {
        List<Object[]> positionList = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();

        for (JsonElement element : ConfigFiles.elementArray) {
            float defaultScale = configFile.getAsJsonObject().get("default_size").getAsFloat();
            JsonObject x = element.getAsJsonObject();
            if (x.get("enabled").getAsBoolean()) {
                String parsedText = x.get("name").getAsString();

                int paddingY = 0;
                int paddingX = 0;

                float setScale = x.get("advanced").getAsJsonObject().get("scale").getAsFloat();
                if (setScale != 0) {
                    defaultScale = setScale;
                }

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();
                }

                int boxWidth = Math.round((client.textRenderer.getWidth(parsedText) + paddingX * 2) * defaultScale);
                int boxHeight = Math.round((9 + 1 + paddingY * 2) * defaultScale);

                int posX = new coordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), boxWidth, 0, "X");
                int posY = new coordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), boxHeight, 0, "Y");

                Object[] sublist = {element, posX, posY, posX + boxWidth, posY + boxHeight};
                positionList.add(sublist);
            }
        }
        return positionList;
    }
}