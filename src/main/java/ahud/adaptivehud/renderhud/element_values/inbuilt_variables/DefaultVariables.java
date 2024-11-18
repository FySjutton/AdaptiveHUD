package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.RequiresAttributes;
import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import com.mojang.blaze3d.platform.GlDebugInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.glfw.GLFW;

import static ahud.adaptivehud.AdaptiveHUD.complexVARS;

public class DefaultVariables {
//    MinecraftClient client = MinecraftClient.getInstance();
//    PlayerEntity player = MinecraftClient.getInstance().player;
//    BlockPos playerPos = client.player.getBlockPos();
//
//    @RequiresAttributes
//    public PlayerEntity player() {
//        return player;
//    }
//
////    @RequiresAttributes
////    public PlayerEntity targetentity() {
////        return player;
////    }
//
//    // ----- BETA.5 BELOW
//
//
//    // ----- BETA.3 BELOW
//
//
//
//
//
//
//
//    public String simulation_distance() {
//        return String.valueOf(client.options.getSimulationDistance().getValue());
//    }
//
//    public String loaded_entities() {
//        return String.valueOf(client.world.getRegularEntityCount());
//    }
//
//
//    public String render_distance() {
//        return String.valueOf((int) client.worldRenderer.getViewDistance());
//    }
//
//    // ----- BETA.2 BELOW
//    public String target_block() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
//        return targetBlock == null ? null : String.valueOf(Registries.BLOCK.getEntry(client.world.getBlockState(targetBlock.getBlockPos()).getBlock()).value().getName().getString());
//    }
//
//    public String target_block_id() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
//        return targetBlock == null ? null : String.valueOf(Registries.BLOCK.getId(client.world.getBlockState(targetBlock.getBlockPos()).getBlock()));
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tbx() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
//        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().x);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tby() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
//        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().y);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tbz() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
//        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().z);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tb_distance() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
//        return targetBlock == null ? null : String.valueOf(Math.sqrt(targetBlock.squaredDistanceTo(player)));
//    }
//
//    public String target_fluid() {
//        String fluidID = target_fluid_id();
//        return fluidID != null ? WordUtils.capitalizeFully(target_fluid_id().split(":")[1].replaceAll("_", " ")) : null;
//    }
//
//    public String target_fluid_id() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
//        return targetBlock == null ? null : String.valueOf(Registries.FLUID.getId(client.world.getFluidState(targetBlock.getBlockPos()).getFluid()));
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tfx() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
//        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().x);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tfy() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
//        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().y);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tfz() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
//        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().z);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tf_distance() {
//        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
//        return targetBlock == null ? null : String.valueOf(Math.sqrt(targetBlock.squaredDistanceTo(player)));
//    }
//
//    public String target_entity() {
//        return client.targetedEntity == null ? null : client.targetedEntity.getType().getName().getString();
//    }
//
//    public String target_entity_id() {
//        return client.targetedEntity == null ? null : String.valueOf(Registries.ENTITY_TYPE.getId(client.targetedEntity.getType()));
//    }
//
//    public String ten() {
//        return client.targetedEntity == null ? null : client.targetedEntity.getName().getString();
//    }
//
//    public String teu() {
//        return client.targetedEntity == null ? null : client.targetedEntity.getUuidAsString();
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tex() {
//        return String.valueOf(client.targetedEntity == null ? null : client.targetedEntity.getPos().x);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tey() {
//        return String.valueOf(client.targetedEntity == null ? null : client.targetedEntity.getPos().y);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String tez() {
//        return String.valueOf(client.targetedEntity == null ? null : client.targetedEntity.getPos().z);
//    }
//
//    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
//    public String te_distance() {
//        Entity targetEntity = client.targetedEntity;
//        return targetEntity == null ? null : String.valueOf(Math.sqrt(targetEntity.squaredDistanceTo(player)));
//    }
//
//
//
//
//
//
//    // ---- BETA.1 BELOW
//
//
//
//    public String biome_blend() {
//        return String.valueOf(client.options.getBiomeBlendRadius().getValue());
//    }
//
//    public String clouds() {
//        return client.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? "off" : (client.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? "fast" : "fancy");
//    }
//
//    public String graphics_quality() {
//        return String.valueOf(client.options.getGraphicsMode().getValue());
//    }
//
//    public String vsync() {
//        return String.valueOf(client.options.getEnableVsync().getValue());
//    }
//
//    public String max_fps() {
//        int maxfps = client.options.getMaxFps().getValue();
//        return maxfps == 260 ? "unlimited" : String.valueOf(maxfps);
//    }
//
//    public String in_powered_snow() {
//        return String.valueOf(client.player.inPowderSnow);
//    }
//
//    public String fall_distance() {
//        return String.valueOf(player.fallDistance);
//    }
//
//    public String player_height() {
//        return String.valueOf(player.getHeight());
//    }
//
//    public String online_time() {
//        return String.valueOf(player.age);
//    }
//
//    public String on_fire() {
//        return String.valueOf(player.wasOnFire);
//    }
//

}