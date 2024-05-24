package fy17.sjuttverse;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.DecimalFormat;

import java.lang.reflect.Method;

public class VariableParser {
    PlayerEntity player = MinecraftClient.getInstance().player;
    Class<?> variablesClass = Variables.class;

    public String parseVariable(String text) {
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)(?::((?:\\w{1,5}=\\w{1,7})(?:,\\w{1,5}=\\w{1,7})*))?\\}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String attributes = matcher.group(2);

            try {
                Method method = variablesClass.getMethod("get_" + varName);
                Variables variables = new Variables();

                String replacement = String.valueOf(method.invoke(variables));
                if (attributes != null) {
                    for (String x : attributes.split(",")) {
                        String[] settValues = x.split("=");
                        if (settValues[0].equals("R")) {
                            DecimalFormat decimalFormat = new DecimalFormat();
                            decimalFormat.setMinimumFractionDigits(Integer.parseInt(settValues[1]));
                            decimalFormat.setMaximumFractionDigits(Integer.parseInt(settValues[1]));
                            replacement = decimalFormat.format(Float.parseFloat(replacement));
                        } else if (settValues[0].equals("S")) {
                            if (Boolean.parseBoolean(settValues[1])) {
                                replacement = replacement.split(":")[1];
                            }
                        }
                    }
                }

                matcher.appendReplacement(result, replacement);


            } catch (Exception e) {
                matcher.appendReplacement(result, "#Error!");
//                System.out.println(e);
            }


//            Variables variables = new Variables(); // Create an instance of Variables
//            String replacement = (String) variables.getClass().getDeclaredMethod("getFPS").invoke(variables);

//            String replacement = variables.getOrDefault(varName, "${" + varName + "}");

//            if (attributes != null) {
//                for (String x : attributes.split(",")) {
//                    String[] settValues = x.split("=");
////                    System.out.println(Arrays.toString(settValues));
//                    if (settValues[0].equals("R")) {
//                        DecimalFormat decimalFormat = new DecimalFormat();
//                        decimalFormat.setMinimumFractionDigits(Integer.parseInt(settValues[1]));
//                        decimalFormat.setMaximumFractionDigits(Integer.parseInt(settValues[1]));
//                        replacement = decimalFormat.format(Float.parseFloat(replacement));
//                    } else if (settValues[0].equals("S")) {
//                        if (Boolean.parseBoolean(settValues[1])) {
//                            replacement = replacement.split(":")[1];
//                        }
//                    }
//                }
//            }
//            try {
//                matcher.appendReplacement(result, replacement);
//            } catch(Exception e) {
//                matcher.appendReplacement(result, "#ERROR!");
//            }
        }
//        matcher.appendTail(result);
        matcher.appendTail(result);
        return String.valueOf(result);
    }
}