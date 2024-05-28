package fy17.sjuttverse;

import fy17.sjuttverse.configscreen.ConfigScreen;
import fy17.sjuttverse.renderhud.RenderHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

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
		HudRenderCallback.EVENT.register(new RenderHUD()::renderCustomHud);
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




}