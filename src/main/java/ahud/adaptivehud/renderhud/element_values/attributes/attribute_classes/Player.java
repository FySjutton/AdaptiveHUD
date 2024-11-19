package ahud.adaptivehud.renderhud.element_values.attributes.attribute_classes;

import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class Player {
    private final PlayerEntity player;

    public Player(PlayerEntity player) {
        this.player = player;
    }
    public Player(ClientPlayerEntity player) {this.player = player; }

    public String display_name() {
        Text displayName = player.getDisplayName();
        return displayName != null ? displayName.getString() : player.getGameProfile().getName();
    }

    public String name() {
        return player.getNameForScoreboard();
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String x() {
        return String.valueOf(player.getX());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String y() {
        return String.valueOf(player.getY());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String z() {
        return String.valueOf(player.getZ());
    }

    public String health() {
        return String.valueOf(player.getHealth());
    }

    public String max_health() {
        return String.valueOf(player.getMaxHealth());
    }

    public String hunger() {
        return String.valueOf(player.getHungerManager().getFoodLevel());
    }

    public String saturation() {
        return String.valueOf(player.getHungerManager().getSaturationLevel());
    }

    public String xp_level() {
        return String.valueOf(player.experienceLevel);
    }

    public String xp() {
        return String.valueOf(player.totalExperience);
    }

    public String xp_needed() {
        return String.valueOf(player.getNextLevelExperience());
    }

    public String xp_percentage() {
        return String.valueOf(player.experienceProgress);
    }

    public ItemStack slot(@LocalFlagName("SLOT") String slot) {
        return player.playerScreenHandler.slots.get(Integer.parseInt(slot)).getStack();
    }

    public ItemStack main_hand() {
        return player.getMainHandStack();
    }

    public ItemStack off_hand() {
        return player.getOffHandStack();
    }

    public Entity riding_entity() {
        return player.getVehicle();
    }

//    public String template() {
//        return String.valueOf(player.);
//    }

    public String hand_swinging() {
        return String.valueOf(player.handSwinging);
    }

    public String in_powder_snow() {
        return String.valueOf(player.inPowderSnow);
    }

    public String on_fire() {
        return String.valueOf(player.isOnFire());
    }

    public String spectator() {
        return String.valueOf(player.isSpectator());
    }

    public String creative() {
        return String.valueOf(player.isCreative());
    }

    public String sprinting() {
        return String.valueOf(player.isSprinting());
    }

    public String sneaking() {
        return String.valueOf(player.isSneaking());
    }

    public String swimming() {
        return String.valueOf(player.isSwimming());
    }

    public String in_swimming_pose() {
        return String.valueOf(player.isInSwimmingPose());
    }

    public String in_water() {
        return String.valueOf(player.isSubmergedInWater());
    }

    public String on_ground() {
        return String.valueOf(player.isOnGround());
    }

    public Entity explodedBy() {
        return player.explodedBy;
    }

    public String luck() {
        return String.valueOf(player.getLuck());
    }

    public String invulnerable() {
        return String.valueOf(player.isInvulnerable());
    }

    public String height() {
        return String.valueOf(player.getHeight());
    }

    public String hurt_by_water() {
        return String.valueOf(player.hurtByWater());
    }

    public String baby() {
        return String.valueOf(player.isBaby());
    }

    public String climbing() {
        return String.valueOf(player.isClimbing());
    }

    public String invisible() {
        return String.valueOf(player.isInvisible());
    }

    public String fire_immune() {
        return String.valueOf(player.isFireImmune());
    }

    public String in_lava() {
        return String.valueOf(player.isInLava());
    }

    public String sleeping() {
        return String.valueOf(player.isSleeping());
    }

    public String glowing() {
        return String.valueOf(player.isGlowing());
    }

    public Entity attacker() {
        return player.getAttacker();
    }

    public Entity attacking() {
        return player.getAttacking();
    }

    public String jump_modifier() {
        return String.valueOf(player.getJumpBoostVelocityModifier());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String yaw() {
        return String.valueOf(player.getYaw());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String pitch() {
        return String.valueOf(player.getPitch());
    }

    public String can_hit() {
        return String.valueOf(player.canHit());
    }
}