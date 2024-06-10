package fy17.adaptivehud.renderhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.util.Date;

import static net.minecraft.loot.LootDataType.stream;

public class Variables {
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerEntity player = MinecraftClient.getInstance().player;
    BlockPos playerPos = client.player.getBlockPos();
    public String get_fps() {return String.valueOf(client.getCurrentFps());}
    public String get_x() {return String.valueOf(player.getX());}
    public String get_y() {return String.valueOf(player.getY());}
    public String get_z() {return String.valueOf(player.getZ());}
    public String get_biome() {return String.valueOf(client.world.getBiome(playerPos).getKey().get().getValue());}
    public String get_sky_light() {return String.valueOf(client.world.getLightLevel(LightType.SKY, playerPos));}
    public String get_block_light() {return String.valueOf(client.world.getLightLevel(LightType.BLOCK, playerPos));}
    public String get_tps() {return "";}

    public String get_entities() {
        int entities = 0;
        for (Entity entity : MinecraftClient.getInstance().world.getEntities()) { entities++; }
        return String.valueOf(entities);
    }
    public String get_chunk_x() {return String.valueOf(player.getChunkPos().x);} // Chunk coord X
    public String get_chunk_z() {return String.valueOf(player.getChunkPos().z);} // Chunk coord z
    public String get_player_name() {return client.player.getNameForScoreboard();} // The name of the player
    public String get_player_uuid() {return client.player.getUuidAsString();} // The player's uuid
    public String get_velocity_XZ() {return String.valueOf(Math.abs(client.player.getVelocity().x) * 20 + Math.abs(client.player.getVelocity().z) * 20);} // Idk if it works
    public String get_fall_distance() {return String.valueOf(player.fallDistance);} // What height the player is on (when falling)
    public String get_player_height() {return String.valueOf(player.getHeight());} // How tall the player is
    public String get_online_time() {return String.valueOf(player.age);} // Ticks since player logged in
    public String get_on_fire() {return String.valueOf(player.wasOnFire);} // Boolean, if the player is on fire (survival)
    public String get_server_ip() {
        if (player.getServer() != null) {
            return String.valueOf(player.getServer().getServerIp());
        } else {
            return "No server";
        }
    }


//    SPEED
//    return String.valueOf(Math.abs(client.player.getVelocity().x) * 20 + (client.player.isOnGround() ? 0 : Math.abs(client.player.getVelocity().y) * 20)) + Math.abs(client.player.getVelocity().z) * 20;
}