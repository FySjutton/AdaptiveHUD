package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ServerAndWorld {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PlayerEntity player = client.player;

    public String server_ip() {
        if (player.getServer() != null) {
            return String.valueOf(player.getServer().getServerIp());
        } else {
            return Text.translatable("adaptivehud.variable.noServerFound").getString();
        }
    }

    public String ping() {
        if (client.getNetworkHandler() != null) {
            return String.valueOf(client.getNetworkHandler().getPlayerListEntry(client.player.getUuid()).getLatency());
        }
        return "-1";
    }

    public String tx() {
        return String.valueOf(client.getNetworkHandler().getConnection().getAveragePacketsSent());
    }

    public String rx() {
        return String.valueOf(client.getNetworkHandler().getConnection().getAveragePacketsReceived());
    }
}
