package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import static ahud.adaptivehud.AdaptiveHUD.complexVARS;

public class Movement {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PlayerEntity player = client.player;

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String velocity_x() {
        return String.valueOf(complexVARS.changeX);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String velocity_y() {
        return String.valueOf(complexVARS.changeY);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String velocity_z() {
        return String.valueOf(complexVARS.changeZ);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String velocity_xz() {
        return String.valueOf(Math.sqrt(Math.pow(complexVARS.changeX, 2) + Math.pow(complexVARS.changeZ, 2)));
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String velocity_xyz() {
        return String.valueOf(Math.sqrt(Math.pow(Math.sqrt(Math.pow(complexVARS.changeX, 2) + Math.pow(complexVARS.changeZ, 2)), 2) + Math.pow(complexVARS.changeY, 2)));
    }

    public String on_ground() {
        return String.valueOf(player.isOnGround());
    }

    public String flying() {
        return String.valueOf(player.getAbilities().flying);
    }

    public String sprinting() {
        return String.valueOf(player.isSprinting());
    }

    public String sneaking() {
        return String.valueOf(client.player.isSneaking());
    }

    public String swimming() {
        return String.valueOf(player.isSwimming());
    }

    public String swimming_pose() {
        return String.valueOf(player.isInSwimmingPose());
    }
}
