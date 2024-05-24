package fy17.sjuttverse;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class SjuttverseClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");

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
		String[][] elements = {
			// Text, paddingX, paddingY, posX, posY, textColor, boxColor, shadow
			{"FPS: ${fps}, light sky ${sky_light} light block ${block_light}", "5", "5", "50", "200", "255,255,255,1", "0,0,0,0.3", "true"},
			{"Position: X: ${x:R4}, Y: ${y:R3}, Z: ${z:D2}", "10", "10", "80", "150", "0,255,55,0.7", "5,24,255,0.4", "true"},
			{"Biome: ${biome}", "10", "10", "0", "50", "255,255,255,1", "0,0,0,0.3", "true"}
		};
//		${varName[:(Rantaldecimaler|)]

		for (String[] x : elements) {
			String parsedText = parser.parseVariable(x[0]);
			drawContext.fill(
				Integer.parseInt(x[3]),
				Integer.parseInt(x[4]),
				Integer.parseInt(x[3]) + client.textRenderer.getWidth(parsedText) + 2 * Integer.parseInt(x[1]),
				Integer.parseInt(x[4]) + client.textRenderer.fontHeight + 2 * Integer.parseInt(x[2]),
				parseColor(x[6])
			);

			drawContext.drawText(
				client.textRenderer,
				parsedText,
				Integer.parseInt(x[3]) + Integer.parseInt(x[1]),
				Integer.parseInt(x[4]) + Integer.parseInt(x[2]) + 1,
				parseColor(x[5]),
				Boolean.parseBoolean(x[7])
			);
		}
	}
}