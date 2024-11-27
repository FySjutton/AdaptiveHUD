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

//    public String ordinal_facing() {
//        // Get player's yaw and normalize it to 0–360 degrees
//        float yaw = client.player.getYaw() % 360;
//        if (yaw < 0) yaw += 360;
//
//        // Determine direction based on yaw
//        if (yaw >= 337.5 || yaw < 22.5) return "N";
//        else if (yaw >= 22.5 && yaw < 67.5) return "NE";
//        else if (yaw >= 67.5 && yaw < 112.5) return "E";
//        else if (yaw >= 112.5 && yaw < 157.5) return "SE";
//        else if (yaw >= 157.5 && yaw < 202.5) return "S";
//        else if (yaw >= 202.5 && yaw < 247.5) return "SW";
//        else if (yaw >= 247.5 && yaw < 292.5) return "W";
//        else if (yaw >= 292.5 && yaw < 337.5) return "NW";
//        return "Unknown";
//
//    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String yaw() {
        return String.valueOf(MathHelper.wrapDegrees(player.getYaw()));
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String pitch() {
        return String.valueOf(MathHelper.wrapDegrees(player.getPitch()));
    }
}
