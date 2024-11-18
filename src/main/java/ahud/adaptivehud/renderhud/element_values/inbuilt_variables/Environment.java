package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionTypes;

public class Environment {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PlayerEntity player = client.player;

    public String biome() {
        return String.valueOf(client.world.getBiome(player.getBlockPos()).getKey().get().getValue());
    }

    public String light() {
        return String.valueOf(client.world.getChunkManager().getLightingProvider().getLight(player.getBlockPos(), 0));
    }

    public String sky_light() {
        return String.valueOf(client.world.getLightLevel(LightType.SKY, player.getBlockPos()));
    }

    public String block_light() {
        return String.valueOf(client.world.getLightLevel(LightType.BLOCK, player.getBlockPos()));
    }

    public String dimension() {
        return String.valueOf(player.getWorld().getRegistryKey().getValue().toString());
    }

    public String overworld() {
        return String.valueOf(player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.OVERWORLD); // OVERWORLD.CAVES?
    }

    public String nether() {
        return String.valueOf(player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_NETHER);
    }

    public String the_end() {
        return String.valueOf(player.getWorld().getDimensionEntry().getKey().get() == DimensionTypes.THE_END);
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

    public String multiplayer() {
        return String.valueOf(!client.isInSingleplayer());
    }

    public String singleplayer() {
        return String.valueOf(client.isInSingleplayer());
    }
}
