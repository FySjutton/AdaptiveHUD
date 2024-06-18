package ahud.adaptivehud.renderhud;

import net.minecraft.text.Text;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.adaptivehud.LOGGER;

public class VariableParser {
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
                matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.error").getString());
            }
        }
        matcher.appendTail(result);

        Pattern mathPattern = Pattern.compile("%([\\d+\\-*\\/^ a-z]*)%");
        Matcher mathMatcher = mathPattern.matcher(String.valueOf(result));
        StringBuffer mathResult = new StringBuffer();
        while (mathMatcher.find()) {
            try {
                Expression expression = new ExpressionBuilder(mathMatcher.group(1)).build();
                double replacement = expression.evaluate();
                mathMatcher.appendReplacement(mathResult, String.valueOf(replacement));
            } catch (Exception e) {
                mathMatcher.appendReplacement(mathResult, Text.translatable("adaptivehud.variable.error").getString());
            }
        }
        mathMatcher.appendTail(mathResult);
        return String.valueOf(mathResult);
    }
}