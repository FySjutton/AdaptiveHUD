package ahud.adaptivehud;

import ahud.adaptivehud.renderhud.RenderHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static ahud.adaptivehud.adaptivehud.LOGGER;

public class adaptivehudClient implements ClientModInitializer {
	public static final KeyBinding reloadElementsKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			// TRANSLATION KEYS SHOULD BE USED; RESOURCE FILES; CHANGE: https://fabricmc.net/wiki/tutorial:keybinds
			"Reload Elements",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"AdaptiveHUD"
	));
	public static final KeyBinding reloadConfigKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			// TRANSLATION KEYS SHOULD BE USED; RESOURCE FILES; CHANGE: https://fabricmc.net/wiki/tutorial:keybinds
			"Reload Config",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"AdaptiveHUD"
	));
	public static final KeyBinding openConfigKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			// TRANSLATION KEYS SHOULD BE USED; RESOURCE FILES; CHANGE: https://fabricmc.net/wiki/tutorial:keybinds
			"Open Config",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_C,
			"AdaptiveHUD"
	));

	@Override
	public void onInitializeClient() {
		new ConfigFiles().CheckDefaultConfigs();
		new ConfigFiles().generateConfigArray();
		new ConfigFiles().GenerateElementArray();

		HudRenderCallback.EVENT.register(new RenderHUD()::renderCustomHud);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (reloadElementsKeyBind.wasPressed()) {
				new ConfigFiles().GenerateElementArray();
			}
			if (reloadConfigKeyBind.wasPressed()) {
				new ConfigFiles().generateConfigArray();
			}
			if (openConfigKeyBind.wasPressed()) {
				LOGGER.info("Loading?");
//				new ConfigScreen().init();

//				MinecraftClient.getInstance().player.openHandledScreen((NamedScreenHandlerFactory) new ConfigScreen());
			}
		});
	}




}