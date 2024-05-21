package fy17.sjuttverse;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.text.Text;

import static fy17.sjuttverse.Sjuttverse.LOGGER;
import static net.minecraft.text.Text.*;

public class SjuttverseClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");
	}
}
