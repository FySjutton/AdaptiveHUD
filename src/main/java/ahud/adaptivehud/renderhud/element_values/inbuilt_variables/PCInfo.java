package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import com.mojang.blaze3d.platform.GlDebugInfo;
import net.minecraft.client.MinecraftClient;

public class PCInfo {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public String gpu_version() {
        return GlDebugInfo.getVersion();
    }

    public String gpu_name() {
        return GlDebugInfo.getRenderer();
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String gpu() {
        return String.valueOf(client.getGpuUtilizationPercentage());
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

}
