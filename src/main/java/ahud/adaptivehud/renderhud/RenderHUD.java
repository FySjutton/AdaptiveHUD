package ahud.adaptivehud.renderhud;

import ahud.adaptivehud.renderhud.element_values.ValueParser;
import ahud.adaptivehud.Tools;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ahud.adaptivehud.ConfigFiles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;

import static ahud.adaptivehud.AdaptiveHUD.*;
import static ahud.adaptivehud.ConfigFiles.configFile;
import static ahud.adaptivehud.AdaptiveHUD.renderElements;

public class RenderHUD {
    private long lastAdvancedUpdate = 0;

    private final boolean USE_VALUE;
    private final int RELOAD_COOLDOWN = configFile.getAsJsonObject().get("variable_reload_cooldown").getAsInt();

    public RenderHUD(boolean useValue) {
        this.USE_VALUE = useValue;
    }

    public void renderCustomHud(DrawContext drawContext) {
        if (!renderElements && this.USE_VALUE) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();

        if (!configFile.getAsJsonObject().get("render_on_debug").getAsBoolean()) {
            if (client.inGameHud.getDebugHud().shouldShowDebugHud()) {
                return;
            }
        }

        if (!configFile.getAsJsonObject().get("render_on_f1").getAsBoolean()) {
            if (client.options.hudHidden) {
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
                    String renderReq = x.get("advanced").getAsJsonObject().get("renderRequirement").getAsString();
                    if (!renderReq.isEmpty()) {
                        boolean render = parser.renderCheck(renderReq);
                        if (!render) {
                            continue;
                        } else {
                            parsedText = parser.parseValue(x.get("value").getAsString());
                        }
                    } else {
                        parsedText = parser.parseValue(x.get("value").getAsString());
                    }
                } else {
                    parsedText = x.get("name").getAsString();
                }
                if (parsedText.isEmpty()) {
                    return;
                }

                ArrayList<String> texts = new ArrayList<>(Arrays.asList(parsedText.split("\n")));
                texts.removeAll(Collections.singleton(""));
                if (texts.isEmpty()) {
                    continue;
                }
                ArrayList<Integer> widths = new ArrayList<>();
                for (String text : texts) {
                    widths.add(client.textRenderer.getWidth(text));
                }
                int maxWidth = Collections.max(widths);
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
                    trueScale = configFile.getAsJsonObject().get("default_size").getAsFloat();
                    matrices.scale(trueScale, trueScale, 1);
                }

                Tools tools = new Tools();

                if (x.get("background").getAsJsonObject().get("enabled").getAsBoolean()) {
                    paddingX = x.get("background").getAsJsonObject().get("paddingX").getAsInt();
                    paddingY = x.get("background").getAsJsonObject().get("paddingY").getAsInt();

                    posX = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), maxWidth + paddingX * 2, trueScale, "X");
                    posY = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), texts.size() * 11 - 2 + Math.round(paddingY * 2), trueScale, "Y");

                    drawContext.fill(
                        posX,
                        posY,
                        posX + maxWidth + 2 * paddingX,
                        posY + texts.size() * 11 - 2 + Math.round(2 * paddingY),
                        tools.parseColor(x.get("background").getAsJsonObject().get("backgroundColor").getAsString())
                    );
                } else {
                    posX = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posX").getAsInt(), client.getWindow().getScaledWidth(), maxWidth, trueScale,"X");
                    posY = new CoordCalculators().getActualCords(element.getAsJsonObject(), x.get("posY").getAsInt(), client.getWindow().getScaledHeight(), texts.size() * 11 - 2, trueScale,"Y");
                }
                int extra = 0;
                for (int i = 0; i < texts.size(); i++) {
                    drawContext.drawText(
                        client.textRenderer,
                        texts.get(i),
                        posX + new CoordCalculators().getTextAlignX(element.getAsJsonObject(), maxWidth + 2 * paddingX, widths.get(i), paddingX),
                        posY + paddingY + extra + 1, // + 1, looks wrong otherwise?
                        tools.parseColor(x.get("textColor").getAsString()),
                        x.get("shadow").getAsBoolean()
                    );
                    extra += 11;
                }

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