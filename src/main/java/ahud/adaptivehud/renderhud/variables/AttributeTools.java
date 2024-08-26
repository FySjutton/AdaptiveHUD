package ahud.adaptivehud.renderhud.variables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AttributeTools {
    public String roundNum(float value, int decimals) {
        DecimalFormat decimalFormat = new DecimalFormat();

        DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.US);
        decimalFormat.setDecimalFormatSymbols(decimalSymbol);

        decimalFormat.setMinimumFractionDigits(decimals);
        decimalFormat.setMaximumFractionDigits(decimals);
        decimalFormat.setGroupingUsed(false); // Removes spacing in format: 1 000 -> 1000

        return decimalFormat.format(value);
    }

    public HitResult targetBlock(boolean include_fluids) {
        HitResult blockHit = MinecraftClient.getInstance().player.raycast(20.0, 0.0F, include_fluids);
        return blockHit.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK ? blockHit : null;
    }
}