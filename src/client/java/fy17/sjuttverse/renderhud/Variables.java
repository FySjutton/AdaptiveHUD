package fy17.sjuttverse.renderhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class Variables {
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerEntity player = MinecraftClient.getInstance().player;
    BlockPos playerPos = client.player.getBlockPos();
    public String get_fps() {return client.fpsDebugString.split(" ")[0];}
    public String get_x() {return String.valueOf(player.getX());}
    public String get_y() {return String.valueOf(player.getY());}
    public String get_z() {return String.valueOf(player.getZ());}
    public String get_biome() {return String.valueOf(client.world.getBiome(playerPos).getKey().get().getValue());}
    public String get_sky_light() {return String.valueOf(client.world.getLightLevel(LightType.SKY, playerPos));}
    public String get_block_light() {return String.valueOf(client.world.getLightLevel(LightType.BLOCK, playerPos));}
    public String get_tps() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}
//    public String get_tpsp() {return "";}


}