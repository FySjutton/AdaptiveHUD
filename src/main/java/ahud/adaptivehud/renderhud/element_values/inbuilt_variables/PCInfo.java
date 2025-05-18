package ahud.adaptivehud.renderhud.element_values.inbuilt_variables;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;


import static ahud.adaptivehud.AdaptiveHUD.complexVARS;

public class PCInfo {
    private final MinecraftClient client = MinecraftClient.getInstance();

    public String gpu_version() {
        return GL11.glGetString(GL11.GL_VERSION);
    }

    public String gpu_name() {
        return GL11.glGetString(GL11.GL_RENDERER);
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String gpu() {
        return String.valueOf(client.getGpuUtilizationPercentage());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"0"})
    public String cpu() { // this is just a personal beta, public 1.21.4 might not include it
        return String.valueOf(complexVARS.cpuLoad);
    }

    public String display_vendor() {
        return GL11.glGetString(GL11.GL_VENDOR);
    }

    public String display_height() {
        return String.valueOf(MinecraftClient.getInstance().getWindow().getFramebufferHeight());
    }

    public String display_width() {
        return String.valueOf(MinecraftClient.getInstance().getWindow().getFramebufferWidth());
    }

    public String cpu_name() {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        return processor.getProcessorIdentifier().getName();
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

    public String java_bit() {
        return System.getProperty("sun.arch.data.model");
    }
}
