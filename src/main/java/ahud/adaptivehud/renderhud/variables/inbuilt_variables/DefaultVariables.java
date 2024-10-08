package ahud.adaptivehud.renderhud.variables.inbuilt_variables;

import ahud.adaptivehud.renderhud.variables.annotations.RequiresAttributes;
import ahud.adaptivehud.renderhud.variables.annotations.LocalFlagName;
import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlag;
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
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerEntity player = MinecraftClient.getInstance().player;
    BlockPos playerPos = client.player.getBlockPos();

    @RequiresAttributes
    public PlayerEntity player() {
        return player;
    }

//    @RequiresAttributes
//    public PlayerEntity targetentity() {
//        return player;
//    }

    // ----- BETA.5 BELOW
    public String display_name() {
        Text displayName = client.player.getDisplayName();
        return displayName != null ? displayName.getString() : client.player.getGameProfile().getName();
    }

    // ----- BETA.3 BELOW
    public String gpu_version() {
        return GlDebugInfo.getVersion();
    }

    public String gpu_name() {
        return GlDebugInfo.getRenderer();
    }

    public String display_vendor() {
        return GlDebugInfo.getVendor();
    }

    public String display_height() {
        return String.valueOf(MinecraftClient.getInstance().getWindow().getFramebufferHeight());
    }

    public String display_width() {
        return String.valueOf(MinecraftClient.getInstance().getWindow().getFramebufferWidth());
    }

    public String cpu_name() {
        return GlDebugInfo.getCpuInfo();
    }

    public String memory_allocated() {
        long m = Runtime.getRuntime().totalMemory();
        return String.valueOf(m / 1024L / 1024L);
    }

    public String allocated_memory_percent() {
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        return String.valueOf(m * 100L / l);
    }

    public String max_memory() {
        long l = Runtime.getRuntime().maxMemory();
        return String.valueOf(l / 1024L / 1024L);
    }

    public String memory_used() {
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        return String.valueOf(o / 1024L / 1024L);
    }

    public String memory_used_percent() {
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        return String.valueOf(o * 100L / l);
    }

    public String java_version() {
        return System.getProperty("java.version");
    }

    public String light() {
        return String.valueOf(client.world.getChunkManager().getLightingProvider().getLight(playerPos, 0));
    }

    public String forced_loaded_chunks() {
        return String.valueOf(client.player.getWorld() instanceof ServerWorld ? ((ServerWorld) client.player.getWorld()).getForcedChunks() : 0);
    }

    public String simulation_distance() {
        return String.valueOf(client.options.getSimulationDistance().getValue());
    }

    public String loaded_entities() {
        return String.valueOf(client.world.getRegularEntityCount());
    }

    public String buffer_count() {
        return String.valueOf(client.worldRenderer.getChunkBuilder().getFreeBufferCount());
    }

    public String upload_queue() {
        return String.valueOf(client.worldRenderer.getChunkBuilder().getChunksToUpload());
    }

    public String queued_tasks() {
        return String.valueOf(client.worldRenderer.getChunkBuilder().getToBatchCount());
    }

    public String render_distance() {
        return String.valueOf((int) client.worldRenderer.getViewDistance());
    }

    public String chunk_culling_enabled() {
        return String.valueOf(client.chunkCullingEnabled);
    }

    public String loaded_chunks() {
        return String.valueOf((int) client.worldRenderer.getChunkCount());
    }

    public String rendered_chunks() {
        return String.valueOf(client.worldRenderer.getCompletedChunkCount());
    }

    public String moon_phase() {
        return String.valueOf(client.world.getMoonPhase() + 1);
    }

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

    public String mods() {
        return String.valueOf(FabricLoader.getInstance().getAllMods().size());
    }

    // ----- BETA.2 BELOW

    public String key_pressed(@LocalFlagName("KEY") String scancode) {
        // All scancodes can be found at "https://www.glfw.org/docs/3.3/group__keys.html".
        // For example, "R" is 82.
        return String.valueOf(GLFW.glfwGetKey(client.getWindow().getHandle(), Integer.parseInt(scancode)) == GLFW.GLFW_PRESS);
    }

    public String the_end() {
        return String.valueOf(player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END);
    }

    public String nether() {
        return String.valueOf(player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER);
    }

    public String overworld() {
        return String.valueOf(player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD); // OVERWORLD.CAVES?
    }

    public String dimension() {
        return String.valueOf(player.getWorld().getRegistryKey().getValue().toString());
    }

    public String on_ground() {
        return String.valueOf(player.isOnGround());
    }

    public String flying() {
        return String.valueOf(player.getAbilities().flying);
    }

    public String region_x() {
        return String.valueOf(player.getChunkPos().getRegionX());
    }

    public String region_z() {
        return String.valueOf(player.getChunkPos().getRegionZ());
    }

    public String rel_region_x() {
        return String.valueOf(player.getChunkPos().getRegionRelativeX());
    }

    public String rel_region_z() {
        return String.valueOf(player.getChunkPos().getRegionRelativeZ());
    }

    public String rel_chunk_x() {
        return String.valueOf(player.getBlockPos().getX() & 15);
    }

    public String rel_chunk_y() {
        return String.valueOf(player.getBlockPos().getY() & 15);
    }

    public String rel_chunk_z() {
        return String.valueOf(player.getBlockPos().getZ() & 15);
    }

    public String chunk_y() {
        return String.valueOf(ChunkSectionPos.getSectionCoord(player.getBlockPos().getY()));
    }

    public String target_block() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
        return targetBlock == null ? null : String.valueOf(Registries.BLOCK.getEntry(client.world.getBlockState(targetBlock.getBlockPos()).getBlock()).value().getName().getString());
    }

    public String target_block_id() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
        return targetBlock == null ? null : String.valueOf(Registries.BLOCK.getId(client.world.getBlockState(targetBlock.getBlockPos()).getBlock()));
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tbx() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().x);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tby() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().y);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tbz() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().z);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tb_distance() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlock;
        return targetBlock == null ? null : String.valueOf(Math.sqrt(targetBlock.squaredDistanceTo(player)));
    }

    public String target_fluid() {
        String fluidID = target_fluid_id();
        return fluidID != null ? WordUtils.capitalizeFully(target_fluid_id().split(":")[1].replaceAll("_", " ")) : null;
    }

    public String target_fluid_id() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
        return targetBlock == null ? null : String.valueOf(Registries.FLUID.getId(client.world.getFluidState(targetBlock.getBlockPos()).getFluid()));
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tfx() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().x);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tfy() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().y);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tfz() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
        return targetBlock == null ? null : String.valueOf(targetBlock.getPos().z);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tf_distance() {
        BlockHitResult targetBlock = (BlockHitResult) complexVARS.targetBlockFluid;
        return targetBlock == null ? null : String.valueOf(Math.sqrt(targetBlock.squaredDistanceTo(player)));
    }

    public String target_entity() {
        return client.targetedEntity == null ? null : client.targetedEntity.getType().getName().getString();
    }

    public String target_entity_id() {
        return client.targetedEntity == null ? null : String.valueOf(Registries.ENTITY_TYPE.getId(client.targetedEntity.getType()));
    }

    public String ten() {
        return client.targetedEntity == null ? null : client.targetedEntity.getName().getString();
    }

    public String teu() {
        return client.targetedEntity == null ? null : client.targetedEntity.getUuidAsString();
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tex() {
        return String.valueOf(client.targetedEntity == null ? null : client.targetedEntity.getPos().x);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tey() {
        return String.valueOf(client.targetedEntity == null ? null : client.targetedEntity.getPos().y);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String tez() {
        return String.valueOf(client.targetedEntity == null ? null : client.targetedEntity.getPos().z);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String te_distance() {
        Entity targetEntity = client.targetedEntity;
        return targetEntity == null ? null : String.valueOf(Math.sqrt(targetEntity.squaredDistanceTo(player)));
    }

    public String sneaking() {
        return String.valueOf(client.player.isSneaking());
    }

    public String multiplayer() {
        return String.valueOf(!client.isInSingleplayer());
    }
    public String singleplayer() {
        return String.valueOf(client.isInSingleplayer());
    }

    public String snowing() {
        return String.valueOf(client.world.isRaining() && (client.world.getBiome(player.getBlockPos()).value().getPrecipitation(player.getBlockPos()) == Biome.Precipitation.SNOW));
    }

    public String raining() {
        return String.valueOf(client.world.isRaining());
    }

    public String thundering() {
        return String.valueOf(client.world.isThundering());
    }

    // ---- BETA.1 BELOW

    public String tx() {
        return String.valueOf(client.getNetworkHandler().getConnection().getAveragePacketsSent());
    }

    public String rx() {
        return String.valueOf(client.getNetworkHandler().getConnection().getAveragePacketsReceived());
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

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String gpu() {
        return String.valueOf(client.getGpuUtilizationPercentage());
    }

    public String biome_blend() {
        return String.valueOf(client.options.getBiomeBlendRadius().getValue());
    }

    public String clouds() {
        return client.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? "off" : (client.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? "fast" : "fancy");
    }

    public String graphics_quality() {
        return String.valueOf(client.options.getGraphicsMode().getValue());
    }

    public String vsync() {
        return String.valueOf(client.options.getEnableVsync().getValue());
    }

    public String max_fps() {
        int maxfps = client.options.getMaxFps().getValue();
        return maxfps == 260 ? "unlimited" : String.valueOf(maxfps);
    }

    public String client_version_type() {
        return "release".equalsIgnoreCase(this.client.getVersionType()) ? "" : this.client.getVersionType(); // "Fabric"
    }

    public String client_mod_name() {
        return ClientBrandRetriever.getClientModName(); // "fabric"
    }

    public String game_version() {
        return client.getGameVersion(); // "Fabric"
    }

    public String version() {
        return SharedConstants.getGameVersion().getName();
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

    public String in_powered_snow() {
        return String.valueOf(client.player.inPowderSnow);
    }

    public String fps() {
        return String.valueOf(client.getCurrentFps());
    }

    public String mfps() {
        return String.valueOf(1000 / client.getCurrentFps());
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

    public String ping() {
        if (client.getNetworkHandler() != null) {
            return String.valueOf(client.getNetworkHandler().getPlayerListEntry(client.player.getUuid()).getLatency());
        }
        return "-1";
    }

    public String biome() {
        return String.valueOf(client.world.getBiome(playerPos).getKey().get().getValue());
    }

    public String sky_light() {
        return String.valueOf(client.world.getLightLevel(LightType.SKY, playerPos));
    }

    public String block_light() {
        return String.valueOf(client.world.getLightLevel(LightType.BLOCK, playerPos));
    }

    public String chunk_x() {
        return String.valueOf(player.getChunkPos().x);
    }

    public String chunk_z() {
        return String.valueOf(player.getChunkPos().z);
    }

    public String player_name() {
        return client.player.getNameForScoreboard();
    }

    public String player_uuid() {
        return client.player.getUuidAsString();
    }

    public String fall_distance() {
        return String.valueOf(player.fallDistance);
    }

    public String player_height() {
        return String.valueOf(player.getHeight());
    }

    public String online_time() {
        return String.valueOf(player.age);
    }

    public String on_fire() {
        return String.valueOf(player.wasOnFire);
    }

    public String server_ip() {
        if (player.getServer() != null) {
            return String.valueOf(player.getServer().getServerIp());
        } else {
            return Text.translatable("adaptivehud.variable.noServerFound").getString();
        }
    }
}