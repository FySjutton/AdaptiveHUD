package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;

public class Misc {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public String key_pressed(@LocalFlagName("KEY") String scancode) {
        // All scancodes can be found at "https://www.glfw.org/docs/3.3/group__keys.html".
        // For example, "R" is 82.
        return String.valueOf(GLFW.glfwGetKey(client.getWindow().getHandle(), Integer.parseInt(scancode)) == GLFW.GLFW_PRESS);
    }

    public String client_mod_name() {
        return ClientBrandRetriever.getClientModName(); // "fabric"
    }

    public String game_version() {
        return client.getGameVersion(); // "Fabric"
    }

    public String client_version_type() {
        return "release".equalsIgnoreCase(this.client.getVersionType()) ? "" : this.client.getVersionType(); // "Fabric"
    }

    public String version() {
        return SharedConstants.getGameVersion().getName();
    }

    public String mods() {
        return String.valueOf(FabricLoader.getInstance().getAllMods().size());
    }

    public String moon_phase() {
        return String.valueOf(client.world.getMoonPhase() + 1);
    }

    public String chat_open() {
        return String.valueOf(client.currentScreen instanceof ChatScreen);
    }

    public String screen_open() {
        return String.valueOf(client.currentScreen != null);
    }

    public String screen_name() {
        if (client.currentScreen != null) {
            return String.valueOf(!client.currentScreen.getTitle().getString().isBlank() ? client.currentScreen.getTitle().getString() : "null");
        }
        return null;
    }
}
