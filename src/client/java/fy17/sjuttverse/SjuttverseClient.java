package fy17.sjuttverse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import org.lwjgl.glfw.GLFW;

import java.util.Iterator;

import static fy17.sjuttverse.Sjuttverse.LOGGER;

public class SjuttverseClient implements ClientModInitializer {

	public static final KeyBinding reloadElementsKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			// TRANSLATION KEYS SHOULD BE USED; RESOURCE FILES; CHANGE: https://fabricmc.net/wiki/tutorial:keybinds
			"Reload Elements",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"Sjuttverse"
	));
	public static final KeyBinding openConfigKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			// TRANSLATION KEYS SHOULD BE USED; RESOURCE FILES; CHANGE: https://fabricmc.net/wiki/tutorial:keybinds
			"Open Config",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_C,
			"Sjuttverse"
	));

	@Override
	public void onInitializeClient() {
		new ConfigFiles().CheckDefaultConfigs();
		new ConfigFiles().GenerateElementArray();
		HudRenderCallback.EVENT.register(this::renderCustomHud);
		new ConfigScreen();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (reloadElementsKeyBind.wasPressed()) {
				new ConfigFiles().GenerateElementArray();
			}
			if (openConfigKeyBind.wasPressed()) {
				LOGGER.info("Loading?");
//				new ConfigScreen().init();

//				MinecraftClient.getInstance().player.openHandledScreen((NamedScreenHandlerFactory) new ConfigScreen());
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

		Iterator<JsonElement> iterator = ConfigFiles.elementArray.iterator();
		while (iterator.hasNext()) {
			JsonElement element = iterator.next();
			try {
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
			} catch (Exception e) {
				LOGGER.error("Error encountered while loading " + element.getAsJsonObject().get("name") + "!");
				LOGGER.error("Started unloading the file...");
				iterator.remove();
				LOGGER.error("Element has been removed... Please fix the corrupted file manually, and then restart your game. This most likely means a required key is missing. Do not edit element files manually unless you know what you're doing. Error:");
				LOGGER.error(String.valueOf(e));
			}
		}
	}
}