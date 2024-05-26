package fy17.sjuttverse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static fy17.sjuttverse.ConfigFiles.elementArray;

public class SjuttverseClient implements ClientModInitializer {

	public static final KeyBinding myKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.sjuttverse.reloadelements", // Keybind identifier
			InputUtil.Type.KEYSYM, // Key type
			GLFW.GLFW_KEY_Z, // Default key
			"category.sjuttverse" // Keybind category
	));

	@Override
	public void onInitializeClient() {
		new ConfigFiles().CheckDefaultConfigs();
		new ConfigFiles().GenerateElementArray();
		HudRenderCallback.EVENT.register(this::renderCustomHud);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (myKeyBinding.wasPressed()) {
				new ConfigFiles().GenerateElementArray();
			}
		});
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

		for (JsonElement element : elementArray) {
			JsonObject x = element.getAsJsonObject();
			if (x.get("enabled").getAsBoolean()) {
				String parsedText = parser.parseVariable(x.get("value").getAsString());

				boolean loadBackground = x.get("background").getAsJsonObject().get("enabled").getAsBoolean();
				int paddingY = 0;
				int paddingX = 0;

				if (loadBackground) {
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
			}
		}
	}
}