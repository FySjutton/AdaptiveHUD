package fy17.sjuttverse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import static fy17.sjuttverse.ConfigFiles.elementArray;
import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class SjuttverseClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");

		new ConfigFiles().CheckDefaultConfigs();
		new ConfigFiles().GenerateElementArray();
		HudRenderCallback.EVENT.register(this::renderCustomHud);
	}

	private int parseColor(String colorString) {
		String[] rgba = colorString.split(",");
		return (
			(((int) (Float.parseFloat(rgba[3]) * 255)) << 24) |
			(Integer.parseInt(rgba[0]) << 16) |
			(Integer.parseInt(rgba[1]) << 8) |
			Integer.parseInt(rgba[2])
		);
	}

	private void renderCustomHud(DrawContext drawContext, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		VariableParser parser = new VariableParser();
//		String[][] elements = {
//			// Text, paddingX, paddingY, posX, posY, textColor, boxColor, shadow
//			{"FPS: ${fps}, light sky ${sky_light} light block ${block_light}", "5", "5", "15", "0", "255,255,255,1", "0,0,0,0.3", "true"},
//			{"Position: X: ${x:R=4}, Y: ${y:R=3}, Z: ${z:D=2}", "10", "10", "20", "25", "0,255,55,0.7", "5,24,255,0.4", "true"},
//			{"Biome: ${biome:S=true}", "10", "10", "0", "50", "255,255,255,1", "0,0,0,0.3", "true"}
//		};
//		${varName[:(Rantaldecimaler|)]

		for (JsonElement element : elementArray) {
			JsonObject x = element.getAsJsonObject();
			if (x.get("enabled").getAsBoolean()) {
				String parsedText = parser.parseVariable(x.get("value").getAsString());
				drawContext.fill(
					Integer.parseInt(x.get("posX").getAsString()),
					Integer.parseInt(x.get("posY").getAsString()),
					x.get("posX").getAsInt() + client.textRenderer.getWidth(parsedText) + 2 * x.get("background").getAsJsonObject().get("paddingX").getAsInt(),
					x.get("posY").getAsInt() + client.textRenderer.fontHeight + 2 * x.get("background").getAsJsonObject().get("paddingY").getAsInt(),
					parseColor(x.get("background").getAsJsonObject().get("backgroundColor").getAsString())
				);

				drawContext.drawText(
					client.textRenderer,
					parsedText,
					x.get("posX").getAsInt() + x.get("background").getAsJsonObject().get("paddingX").getAsInt(),
					x.get("posY").getAsInt() + x.get("background").getAsJsonObject().get("paddingY").getAsInt() + 1,
					parseColor(x.get("textColor").getAsString()),
					x.get("shadow").getAsBoolean()
				);
			}
		}
	}
}