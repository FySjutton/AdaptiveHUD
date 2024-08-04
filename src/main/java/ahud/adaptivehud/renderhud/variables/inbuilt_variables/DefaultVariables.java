package ahud.adaptivehud.renderhud.variables.inbuilt_variables;

import ahud.adaptivehud.renderhud.variables.AttributeName;
import ahud.adaptivehud.renderhud.variables.AttributeTools;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class DefaultVariables {
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerEntity player = MinecraftClient.getInstance().player;
    BlockPos playerPos = client.player.getBlockPos();

    AttributeTools tools = new AttributeTools();

    public String test() { // For testing purposes, so I don't have to restart my game as often
        String value = "";

        return String.valueOf(value);
    }

    // -----

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

    public String mtps(@AttributeName("R") String round) {
        IntegratedServer server = client.getServer();
        if (server != null) {
            float value = server.getAverageTickTime();
            if (round == null) {round = "0";}
            return tools.roundNum(value, Integer.parseInt(round));
        } else {
            return "-";
        }
    }

    public String gpu(@AttributeName("R") String round) {
        float gpuPercent = (float) client.getGpuUtilizationPercentage();
        if (round == null) {round = "0";}
        return tools.roundNum(gpuPercent, Integer.parseInt(round));
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
    public String yaw(@AttributeName("R") String round) {
        float yawN = MathHelper.wrapDegrees(player.getYaw());
        if (round == null) {round = "0";}
        return tools.roundNum(yawN, Integer.parseInt(round));
    }
    public String pitch(@AttributeName("R") String round) {
        float pitchN = MathHelper.wrapDegrees(player.getPitch());
        if (round == null) {round = "0";}
        return tools.roundNum(pitchN, Integer.parseInt(round));
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

    public String x(@AttributeName("R") String round) {
        float xVal = (float) player.getX();
        if (round == null) {round = "0";}
        return tools.roundNum(xVal, Integer.parseInt(round));
    }

    public String y(@AttributeName("R") String round) {
        float yVal = (float) player.getY();
        if (round == null) {round = "0";}
        return tools.roundNum(yVal, Integer.parseInt(round));
    }

    public String z(@AttributeName("R") String round) {
        float zVal = (float) player.getZ();
        if (round == null) {round = "0";}
        return tools.roundNum(zVal, Integer.parseInt(round));
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

    public String entities() {
        int entities = 0;
        for (Entity entity : MinecraftClient.getInstance().world.getEntities()) { entities++; }
        return String.valueOf(entities);
    }

    public String chunk_x() {
        return String.valueOf(player.getChunkPos().x);
    } // Chunk coord X

    public String chunk_z() {
        return String.valueOf(player.getChunkPos().z);
    } // Chunk coord z

    public String player_name() {
        return client.player.getNameForScoreboard();
    } // The name of the player

    public String player_uuid() {
        return client.player.getUuidAsString();
    } // The player's uuid

//    public String velocity_XZ() {
//        return String.valueOf(Math.abs(client.player.getVelocity().x) * 20 + Math.abs(client.player.getVelocity().z) * 20);
//    } // Idk if it works

    public String fall_distance() {
        return String.valueOf(player.fallDistance);
    } // What height the player is on (when falling)

    public String player_height() {
        return String.valueOf(player.getHeight());
    } // How tall the player is

    public String online_time() {
        return String.valueOf(player.age);
    } // Ticks since player logged in

    public String on_fire() {
        return String.valueOf(player.wasOnFire);
    } // Boolean, if the player is on fire (survival)

    public String server_ip() {
        if (player.getServer() != null) {
            return String.valueOf(player.getServer().getServerIp());
        } else {
            return Text.translatable("adaptivehud.variable.noServerFound").getString();
        }
    }

//    SPEED
//    return String.valueOf(Math.abs(client.player.getVelocity().x) * 20 + (client.player.isOnGround() ? 0 : Math.abs(client.player.getVelocity().y) * 20)) + Math.abs(client.player.getVelocity().z) * 20;
}











