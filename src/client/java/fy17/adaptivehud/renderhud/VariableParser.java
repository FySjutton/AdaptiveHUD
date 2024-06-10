package fy17.adaptivehud.renderhud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableParser {
    PlayerEntity player = MinecraftClient.getInstance().player;
    Class<?> variablesClass = Variables.class;

    public String parseVariable(String text) {
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)(?::(\\w{1,5}=\\w{1,7}(?:,\\w{1,5}=\\w{1,7})*))?}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String attributes = matcher.group(2);

            if (attributes == null) {attributes = "";}

            try {
                Method method = variablesClass.getMethod("get_" + varName);
                Variables variables = new Variables();

                String replacement = String.valueOf(method.invoke(variables));
                if (varName.equals("x") || varName.equals("y") || varName.equals("z")) {
                    if (!attributes.contains("R=")) {
                        attributes = attributes + ",R=3";
                    }
                }
                for (String x : attributes.split("(?<!:),")) {
                    String[] settValues = x.split("=");
                    if (settValues[0].equals("R")) {
                        DecimalFormat decimalFormat = new DecimalFormat();
                        decimalFormat.setMinimumFractionDigits(Integer.parseInt(settValues[1]));
                        decimalFormat.setMaximumFractionDigits(Integer.parseInt(settValues[1]));
                        decimalFormat.setGroupingUsed(false); // Removes spacing in format: 1 000 -> 1000
                        replacement = decimalFormat.format(Float.parseFloat(replacement));
                    } else if (settValues[0].equals("S")) {
                        if (Boolean.parseBoolean(settValues[1])) {
                            replacement = replacement.split(":")[1];
                        }
                    }
                }

                matcher.appendReplacement(result, replacement);
            } catch (Exception e) {
                matcher.appendReplacement(result, "#Error!");
            }
        }
        matcher.appendTail(result);
        return String.valueOf(result);
    }
}