package ahud.adaptivehud.renderhud.variables;

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
}