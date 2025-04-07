package avox.adaptivehud;

import avox.adaptivehud.anchor.AnchorMode;
import avox.adaptivehud.anchor.AnchorPoint;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveHUD implements ModInitializer {
	public static final String MOD_ID = "adaptivehud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final KeyBinding moveScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Move Screen",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V,
			"AdaptiveHUD"
	));

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ElementManager.addElement(new TextElement(null, new AnchorPoint(), "TestingElm1"));

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (moveScreenKeyBind.wasPressed()) {
				client.setScreen(new MoveScreen(Text.of("Move Screen")));
			}
		});
	}
}