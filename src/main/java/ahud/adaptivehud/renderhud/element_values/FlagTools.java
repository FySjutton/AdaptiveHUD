package ahud.adaptivehud.renderhud.element_values;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static ahud.adaptivehud.ConfigFiles.configFile;

public class FlagTools {
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
        double max_distance = configFile.getAsJsonObject().get("max_target_block_distance").getAsDouble();
        HitResult blockHit = MinecraftClient.getInstance().player.raycast(max_distance, 0.0F, include_fluids);
        return blockHit.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK ? blockHit : null;
    }
}