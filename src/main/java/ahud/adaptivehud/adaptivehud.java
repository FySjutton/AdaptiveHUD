package ahud.adaptivehud;

import ahud.adaptivehud.renderhud.RenderHUD;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class adaptivehud implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("adaptivehud");

	public static final KeyBinding reloadElementsKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			Text.translatable("adaptivehud.key.reloadElements").getString(),
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			Text.translatable("adaptivehud.key.category").getString()
	));
	public static final KeyBinding reloadConfigKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			Text.translatable("adaptivehud.key.reloadConfig").getString(),
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			Text.translatable("adaptivehud.key.category").getString()
	));
	public static final KeyBinding openConfigKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			Text.translatable("adaptivehud.key.openConfig").getString(),
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_C,
			Text.translatable("adaptivehud.key.category").getString()
	));

	@Override
	public void onInitialize() {
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
			}
		});
	}
}