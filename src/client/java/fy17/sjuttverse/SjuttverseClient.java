package fy17.sjuttverse;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;

import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class SjuttverseClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");

		HudRenderCallback.EVENT.register(this::renderCustomHud);
	}

	private void renderCustomHud(DrawContext drawContext, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null && client.player != null) {
			String[][] elements = {
					// Text, paddingX, paddingY, posX, posY, textColor, boxColor
					{"Hello!", "5", "5", "50", "200", "#00ff0d", "#ff00e6"},
					{"Goodbye!", "10", "10", "80", "150", "#ffffff", "#00000085"}
			};

			for (String[] x : elements) {
				drawContext.fill(
					0,//Integer.parseInt(x[3]),
					0,//Integer.parseInt(x[4]),
					5,
					//Integer.parseInt(x[3]) + client.textRenderer.getWidth(x[0]) + 2 * Integer.parseInt(x[1]),
					5,//Integer.parseInt(x[4]) + client.textRenderer.fontHeight + 2 * Integer.parseInt(x[2]),
					//(int) Long.parseLong(x[6].substring(1), 16)
						0xff00e6
				);
				drawContext.drawText(
					client.textRenderer,
					x[0],
					Integer.parseInt(x[3]) + Integer.parseInt(x[1]),
					Integer.parseInt(x[4]) + Integer.parseInt(x[2]) + 1,
					(int) Long.parseLong(x[5].substring(1), 16),
					false
				);
			}

//			String text = "Hello, World!";
//
//			int textWidth = client.textRenderer.getWidth(text);
//			int textHeight = client.textRenderer.fontHeight;
//
//			int paddingX = 5;
//			int paddingY = 20;
//
//			int boxX = 50;
//			int boxY = 5;
//
//			int boxColor = 0x90000000;
//			int textColor = 0xFFFFFF;
//
//			drawContext.fill(boxX, boxY, boxX + textWidth + 2 * paddingX, boxY + textHeight + 2 * paddingY, boxColor);
//
//			int textX = boxX + paddingX;
//			int textY = boxY + paddingY + 1;
//
//			drawContext.drawText(client.textRenderer, text, textX, textY, textColor, false);
		}
	}
}