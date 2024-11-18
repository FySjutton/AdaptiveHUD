package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CurrentPlayer {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public String display_name() {
        Text displayName = client.player.getDisplayName();
        return displayName != null ? displayName.getString() : client.player.getGameProfile().getName();
    }

    public String player_name() {
        return client.player.getNameForScoreboard();
    }

    public String player_uuid() {
        return client.player.getUuidAsString();
    }

    public String gamemode() {
        return client.interactionManager.getCurrentGameMode().getName();
    }

    public String survival() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId() == 0);
    }

    public String creative() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId() == 1);
    }

    public String adventure() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId() == 2);
    }

    public String spectator() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId() == 3);
    }

}
