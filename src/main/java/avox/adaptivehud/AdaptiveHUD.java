package avox.adaptivehud;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveHUD implements ModInitializer {
	public static final String MOD_ID = "adaptivehud";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ElementManager.addElement(new TextElement(null, AnchorPoint.TopLeft));
	}
}