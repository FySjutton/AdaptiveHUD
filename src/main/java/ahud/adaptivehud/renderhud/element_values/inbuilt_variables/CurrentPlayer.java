package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.RequiresAttributes;
import ahud.adaptivehud.renderhud.element_values.attributes.attribute_classes.Player;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CurrentPlayer {
    private final MinecraftClient client = MinecraftClient.getInstance();

    @RequiresAttributes
    public PlayerEntity player() {
        return client.player;
    }

    public String display_name() {
        Text displayName = client.player.getDisplayName();
        return displayName != null ? displayName.getString() : client.player.getGameProfile().name();
    }

    public String player_name() {
        return client.player.getNameForScoreboard();
    }

    public String player_uuid() {
        return client.player.getUuidAsString();
    }

    public String gamemode() {
        return client.interactionManager.getCurrentGameMode().getId();
    }

    public String survival() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId().equals("survival"));
    }

    public String creative() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId().equals("creative"));
    }

    public String adventure() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId().equals("adventure"));
    }

    public String spectator() {
        return String.valueOf(client.interactionManager.getCurrentGameMode().getId().equals("spectator"));
    }
}
