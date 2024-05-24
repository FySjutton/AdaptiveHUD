package fy17.sjuttverse;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.DecimalFormat;

public class VariableParser {
    private final Map<String, String> variables;
    PlayerEntity player = MinecraftClient.getInstance().player;

    public VariableParser() {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos playerPos = client.player.getBlockPos();

        this.variables = new HashMap<>();
        this.variables.put("fps", client.fpsDebugString.split(" ")[0]);
        this.variables.put("x", String.valueOf(player.getX()));
        this.variables.put("y", String.valueOf(player.getY()));
        this.variables.put("z", String.valueOf(player.getZ()));
        this.variables.put("sky_light", String.valueOf(client.world.getLightLevel(LightType.SKY, playerPos)));
        this.variables.put("block_light", String.valueOf(client.world.getLightLevel(LightType.BLOCK, playerPos)));
        this.variables.put("biome", String.valueOf(client.world.getBiome(playerPos).getKey().get().getValue()));
    }

    public String parseVariable(String text) {
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)(?::(\\w[\\da-zA-Z,]+))?\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String attributes = matcher.group(2);
            String replacement = variables.getOrDefault(varName, "${" + varName + "}");

            if (attributes != null) {
                for (String x : attributes.split(",")) {
                    if (x.startsWith("R")) {
                        DecimalFormat decimalFormat = new DecimalFormat();
                        decimalFormat.setMinimumFractionDigits(Integer.parseInt(x.substring(1)));
                        decimalFormat.setMaximumFractionDigits(Integer.parseInt(x.substring(1)));
                        replacement = decimalFormat.format(Float.parseFloat(replacement));
                    }
                }
            }
            try {
                matcher.appendReplacement(result, replacement);
            } catch(Exception e) {
                matcher.appendReplacement(result, "#ERROR!");
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }
}