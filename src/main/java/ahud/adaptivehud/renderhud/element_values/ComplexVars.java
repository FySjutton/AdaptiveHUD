package ahud.adaptivehud.renderhud.element_values;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class ComplexVars {
    private final FlagTools tools = new FlagTools();
    private long lastRun = 0;

    private double oldX = 0;
    private double oldY = 0;
    private double oldZ = 0;

    public double changeX;
    public double changeY;
    public double changeZ;

    public HitResult targetBlock;
    public HitResult targetBlockFluid;

    private final HashMap<Integer, Integer> cpsCounter = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void generateCommon() {
        // ALL ELEMENTS ARE LOADED EACH FRAME; EVEN IF THEY WONT BE USED LATER: PERFORMENCE ISSUE!!! FIX
        targetBlock = tools.targetBlock(false);
        targetBlockFluid = tools.targetBlock(true);
    }

    // Same as the one above, but run less frequent.
    public void generateCooldowned() {
        // ALL ELEMENTS ARE LOADED; EVEN IF THEY WONT BE USED LATER: PERFORMENCE ISSUE!!! FIX
        long toPerSecond = 1000 / (Calendar.getInstance().getTimeInMillis() - lastRun);
        lastRun = Calendar.getInstance().getTimeInMillis();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        changeX = Math.abs(player.getX() - oldX) * toPerSecond;
        changeY = Math.abs(player.getY() - oldY) * toPerSecond;
        changeZ = Math.abs(player.getZ() - oldZ) * toPerSecond;
        oldX = player.getX();
        oldY = player.getY();
        oldZ = player.getZ();
    }

    public void cpsClick(int scancode) {
        cpsCounter.put(scancode, cpsCounter.getOrDefault(scancode, 0) + 1);
        scheduler.schedule(() -> {
            cpsCounter.put(scancode, cpsCounter.get(scancode) - 1);
        }, 1, TimeUnit.SECONDS);
    }

    public int getCPSClicks(int scancode) {
        return cpsCounter.getOrDefault(scancode, 0);
    }
}
