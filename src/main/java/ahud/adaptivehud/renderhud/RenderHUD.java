package ahud.adaptivehud.renderhud;

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
                int posX = getFromAnchorPoint(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), client.textRenderer.getWidth(parsedText), "X");
                int posY = getFromAnchorPoint(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), 9 + 1, "Y");

                if (x.has("advanced")) {
                    Float decScale = x.get("advanced").getAsJsonObject().get("scale").getAsFloat();
                    matrices.push();
                    matrices.scale(decScale, decScale, 1);
                }

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();

                    drawContext.fill(
                            posX,
                            posY,
                            posX + client.textRenderer.getWidth(parsedText) + 2 * paddingX,
                            posY + client.textRenderer.fontHeight + 2 * paddingY,
                            parseColor(x.get("background").getAsJsonObject().get("backgroundColor").getAsString())
                    );
                }

                drawContext.drawText(
                        client.textRenderer,
                        parsedText,
                        posX + paddingX,
                        posY + paddingY + 1,
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

    private int getFromAnchorPoint(JsonObject elm, int value, int max, int length, String axis) {
        String anchor = elm.get("alignment").getAsJsonObject().get("anchorPoint" + axis).getAsString();
        String align = elm.get("alignment").getAsJsonObject().get("textAlign" + axis).getAsString();
        int pos;
        if (anchor.equals("center")) {
            pos = max / 2 + value;
        } else if (anchor.equals("bottom") || anchor.equals("right")) {
            pos = max + value;
        } else {
            pos = value;
        }
        if (align.equals("center")) {
            pos -= length / 2;
        } else if (align.equals("bottom") || align.equals("right")) {
            pos -= length;
        }
        return pos;
    }

    public List<Object[]> generatePositions() {
        List<Object[]> positionList = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        VariableParser parser = new VariableParser();

        Float defaultScale = configFile.getAsJsonObject().get("default_size").getAsFloat();
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        matrices.scale(defaultScale, defaultScale, 1);

        for (JsonElement element : ConfigFiles.elementArray) {
            JsonObject x = element.getAsJsonObject();
            if (x.get("enabled").getAsBoolean()) {
                String parsedText = parser.parseVariable(x.get("value").getAsString());

                int paddingY = 0;
                int paddingX = 0;
                int posX2;
                int posY2;
                int posX = getFromAnchorPoint(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), client.textRenderer.getWidth(parsedText), "X");
                int posY = getFromAnchorPoint(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), 9 + 1, "Y");

                if (x.has("advanced")) {
                    Float decScale = x.get("advanced").getAsJsonObject().get("scale").getAsFloat();
                    matrices.push();
                    matrices.scale(decScale, decScale, 1);
                }

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();
                }
                posX2 = posX + client.textRenderer.getWidth(parsedText) + 2 * paddingX;
                posY2 = posY + client.textRenderer.fontHeight + 2 * paddingY;

                if (x.has("advanced")) {
                    matrices.pop();
                }

                Object[] sublist = {element, Math.round(posX * defaultScale), Math.round(posY * defaultScale), Math.round(posX2 * defaultScale), Math.round(posY2 * defaultScale)};
                positionList.add(sublist);
            }
        }
        matrices.pop();
        return positionList;
    }
}