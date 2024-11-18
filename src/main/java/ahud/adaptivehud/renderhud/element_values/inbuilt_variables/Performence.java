package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;

public class Performence {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public String fps() {
        return String.valueOf(client.getCurrentFps());
    }

    public String mfps() {
        return String.valueOf(1000 / client.getCurrentFps());
    }

    public String tps() {
        IntegratedServer server = client.getServer();
        if (server != null) {
            float tps = server.getAverageTickTime();
            return String.valueOf(tps < 50 ? 20 : 1000 / tps);
        } else {
            return "-";
        }
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String mtps() {
        IntegratedServer server = client.getServer();
        return server == null ? "-" : String.valueOf(server.getAverageTickTime());
    }
}
