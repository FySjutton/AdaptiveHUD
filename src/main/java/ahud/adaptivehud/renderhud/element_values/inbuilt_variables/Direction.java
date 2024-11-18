package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class Direction {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PlayerEntity player = client.player;

    public String facing() {
        return client.player.getMovementDirection().asString();
    }

    public String facing_sign() {
        return client.player.getMovementDirection().getDirection().name().equals("POSITIVE") ? "+" : "-";
    }

    public String facing_short() {
        return client.player.getMovementDirection().name().substring(0, 1);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String yaw() {
        return String.valueOf(MathHelper.wrapDegrees(player.getYaw()));
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String pitch() {
        return String.valueOf(MathHelper.wrapDegrees(player.getPitch()));
    }
}
