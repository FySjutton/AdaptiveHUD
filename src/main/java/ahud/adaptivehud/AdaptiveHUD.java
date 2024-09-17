package ahud.adaptivehud;

import ahud.adaptivehud.renderhud.RenderHUD;
import ahud.adaptivehud.renderhud.variables.VariableRegisterer;
import ahud.adaptivehud.renderhud.variables.inbuilt_variables.ComplexVars;
import ahud.adaptivehud.screens.configscreen.ConfigScreen;
import ahud.adaptivehud.screens.movescreen.MoveScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveHUD implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("adaptivehud");
	public static ComplexVars complexVARS = new ComplexVars();
	public static VariableRegisterer variableRegister = new VariableRegisterer();

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
			GLFW.GLFW_KEY_F8,
			Text.translatable("adaptivehud.key.category").getString()
	));
	public static final KeyBinding openMoveScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			Text.translatable("adaptivehud.key.moveElements").getString(),
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			Text.translatable("adaptivehud.key.category").getString()
	));

	private RenderHUD hudRenderer;

	@Override
	public void onInitialize() {
		new ConfigFiles().CheckDefaultConfigs();
		new ConfigFiles().generateConfigArray();
		new ConfigFiles().GenerateElementArray();

		hudRenderer = new RenderHUD(true);

		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> hudRenderer.renderCustomHud(drawContext));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (reloadElementsKeyBind.wasPressed()) {
				new ConfigFiles().GenerateElementArray();
			}
			if (reloadConfigKeyBind.wasPressed()) {
				new ConfigFiles().generateConfigArray();
			}
			if (openConfigKeyBind.wasPressed()) {
				MinecraftClient.getInstance().setScreen(new ConfigScreen(null));
			}
			if (openMoveScreenKeyBind.wasPressed()) {
				MinecraftClient.getInstance().setScreen(new MoveScreen(null, true));
			}
		});

		variableRegister.registerDefaults();
	}
}