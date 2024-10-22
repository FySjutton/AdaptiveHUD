package ahud.adaptivehud.renderhud.variables.inbuilt_variables;

import ahud.adaptivehud.renderhud.variables.FlagTools;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult;

import java.util.Calendar;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class ComplexVars {
    private final FlagTools tools = new FlagTools();
    private long lastRun = 0;

    private double oldX = 0;
    private double oldY = 0;
    private double oldZ = 0;

    protected double changeX;
    protected double changeY;
    protected double changeZ;

    protected HitResult targetBlock;
    protected HitResult targetBlockFluid;

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
}
