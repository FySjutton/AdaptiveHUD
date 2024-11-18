package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;

public class Coordinates {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PlayerEntity player = client.player;

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

    public String chunk_x() {
        return String.valueOf(player.getChunkPos().x);
    }

    public String chunk_y() {
        return String.valueOf(ChunkSectionPos.getSectionCoord(player.getBlockPos().getY()));
    }

    public String chunk_z() {
        return String.valueOf(player.getChunkPos().z);
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


}
