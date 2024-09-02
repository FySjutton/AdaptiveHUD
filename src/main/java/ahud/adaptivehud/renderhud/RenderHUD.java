package ahud.adaptivehud.renderhud;

import ahud.adaptivehud.renderhud.variables.ValueParser;
import ahud.adaptivehud.Tools;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ahud.adaptivehud.ConfigFiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

import static ahud.adaptivehud.AdaptiveHUD.*;
import static ahud.adaptivehud.ConfigFiles.configFile;

public class RenderHUD {
    private long lastAdvancedUpdate = 0;

    private final boolean USE_VALUE;
    private final int RELOAD_COOLDOWN = configFile.getAsJsonObject().get("variable_reload_cooldown").getAsInt();

    public RenderHUD(boolean useValue) {
        this.USE_VALUE = useValue;
    }

    public void renderCustomHud(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!configFile.getAsJsonObject().get("render_on_debug").getAsBoolean()) {
            if (client.inGameHud.getDebugHud().shouldShowDebugHud()) {
                return;
            }
        }

        if (this.USE_VALUE) {
            complexVARS.generateCommon();

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastAdvancedUpdate) > RELOAD_COOLDOWN) {
                complexVARS.generateCooldowned();
                lastAdvancedUpdate = currentTime;
            }
        }


        ValueParser parser = new ValueParser();
        MatrixStack matrices = drawContext.getMatrices();

        for (JsonElement element : ConfigFiles.elementArray) {
            JsonObject x = element.getAsJsonObject();
            if (x.get("enabled").getAsBoolean()) {
                float defaultScale;
                String parsedText;

                if (this.USE_VALUE) {
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
                float trueScale = 1;
                if (setScale != 0) {
                    defaultScale = setScale;
                    // KNOWN BUG SOMEWHERE; SCALING ISN'T PERFECT, AND WHEN ELEMENTS ARE NEXT TO EACHOTHER, THEY SOMETIMES OVERLAPS. (when different scales)
                    float itemHeight = 9 + x.get("background").getAsJsonObject().get("paddingY").getAsInt() * 2;
                    int wantedHeight = Math.round(itemHeight * defaultScale);
                    trueScale = wantedHeight / itemHeight;
                    matrices.scale(trueScale, trueScale, 1);
                } else {
                    defaultScale = configFile.getAsJsonObject().get("default_size").getAsFloat();
                    matrices.scale(defaultScale, defaultScale, 1);
                }

                Tools tools = new Tools();

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();

                    posX = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), client.textRenderer.getWidth(parsedText) + paddingX * 2, trueScale, "X");
                    posY = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), 9 + Math.round(paddingY * 2), trueScale, "Y");

                    drawContext.fill(
                            posX,
                            posY,
                            posX + client.textRenderer.getWidth(parsedText) + 2 * paddingX,
                            posY + 9 + Math.round(2 * paddingY),
                            tools.parseColor(x.get("background").getAsJsonObject().get("backgroundColor").getAsString())
                    );
                } else {
                    posX = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), client.textRenderer.getWidth(parsedText), defaultScale,"X");
                    posY = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), 9, defaultScale,"Y");
                }

                drawContext.drawText(
                        client.textRenderer,
                        parsedText,
                        posX + paddingX,
                        posY + paddingY + 1, // + 1, looks wrong otherwise?
                        tools.parseColor(x.get("textColor").getAsString()),
                        x.get("shadow").getAsBoolean()
                );

                matrices.pop();
            }
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
                int boxHeight = Math.round((9 + paddingY * 2) * defaultScale);

                int posX = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), boxWidth, 0, "X");
                int posY = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), boxHeight, 0, "Y");

                Object[] sublist = {element, posX, posY, posX + boxWidth, posY + boxHeight};
                positionList.add(sublist);
            }
        }
        return positionList;
    }
}