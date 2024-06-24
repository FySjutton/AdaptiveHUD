package ahud.adaptivehud.renderhud.variables.defaults;

import ahud.adaptivehud.renderhud.variables.AttributeName;
import ahud.adaptivehud.renderhud.variables.AttributeTools;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class DefaultVariables {
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerEntity player = MinecraftClient.getInstance().player;
    BlockPos playerPos = client.player.getBlockPos();

    AttributeTools tools = new AttributeTools();

    public String fps() {
        return String.valueOf(client.getCurrentFps());
    }

    public String x(@AttributeName("R") String round) {
        String xVal = String.valueOf(player.getX());
        if (round == null) {round = "0";}
        xVal = tools.roundNum(Float.parseFloat(xVal), Integer.parseInt(round));
        return String.valueOf(xVal);
    }

    public String y(@AttributeName("R") String round) {
        String yVal = String.valueOf(player.getY());
        if (round == null) {round = "0";}
        yVal = tools.roundNum(Float.parseFloat(yVal), Integer.parseInt(round));
        return String.valueOf(yVal);
    }

    public String z(@AttributeName("R") String round) {
        String zVal = String.valueOf(player.getZ());
        if (round == null) {round = "0";}
        zVal = tools.roundNum(Float.parseFloat(zVal), Integer.parseInt(round));
        return String.valueOf(zVal);
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

    public String velocity_XZ() {
        return String.valueOf(Math.abs(client.player.getVelocity().x) * 20 + Math.abs(client.player.getVelocity().z) * 20);
    } // Idk if it works

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