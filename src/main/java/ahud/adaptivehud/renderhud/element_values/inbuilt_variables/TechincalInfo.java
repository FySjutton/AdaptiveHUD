package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;

public class TechincalInfo {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public String buffer_count() {
        return String.valueOf(client.worldRenderer.getChunkBuilder().getFreeBufferCount());
    }

    public String upload_queue() {
        return String.valueOf(client.worldRenderer.getChunkBuilder().getChunksToUpload());
    }

    public String queued_tasks() {
        return String.valueOf(client.worldRenderer.getChunkBuilder().getToBatchCount());
    }

    public String chunk_culling_enabled() {
        return String.valueOf(client.chunkCullingEnabled);
    }

    public String total_chunks() {
        return this.client.world.getChunkManager().getDebugString().split(",")[0];
    }

    public String loaded_chunks() {
        return String.valueOf(this.client.world.getChunkManager().getLoadedChunkCount());
    }

    public String rendered_chunks() {
        return String.valueOf(client.worldRenderer.getCompletedChunkCount());
    }

    public String forced_loaded_chunks() {
        return String.valueOf(client.player.getWorld() instanceof ServerWorld ? ((ServerWorld) client.player.getWorld()).getForcedChunks() : 0);
    }
}
