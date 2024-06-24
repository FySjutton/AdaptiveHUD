package ahud.adaptivehud.renderhud.variables;

import net.minecraft.text.Text;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueParser {
    VariableRegisterer register = new VariableRegisterer();

    public String parseVariable(String text) {
        text = text.replaceAll("&(?=[\\da-fA-Fk-oK-OrR])", "§");

        Pattern pattern = Pattern.compile("\\$\\{(\\w+)(?::(\\w{1,5}=\\w{1,7}(?:,\\w{1,5}=\\w{1,7})*))?((?: *-[a-z]+)*)}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);

            try {
                Method method = register.loadVariable(varName);

                if (method != null) {
                    String attributeString = matcher.group(2);
                    String flagString = matcher.group(3);

                    Parameter[] params = method.getParameters();
                    Object[] parameters = new Object[params.length];
                    for (int i = 0; i < params.length; i++) {
                        Parameter param = params[i];
                        String paramName = ((AttributeName) param.getAnnotations()[0]).value();
                        Object paramValue = null;

                        if (attributeString != null) {
                            for (String x : attributeString.split(",")) {
                                String[] vals = x.split("=");
                                if (paramName.equals(vals[0])) {
                                    paramValue = vals[1];
                                    break;
                                }
                            }
                        }

                        parameters[i] = paramValue;
                    }

                    String varValue = String.valueOf(method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters));

                    varValue = new FlagParser().parseFlags(varValue, flagString);

                    matcher.appendReplacement(result, varValue);
                }


//                Method method = variablesClass.getMethod("get_" + varName);
//                DefaultVariables defaultVariables = new DefaultVariables();
//
//                String replacement = String.valueOf(method.invoke(defaultVariables));
//                if (varName.equals("x") || varName.equals("y") || varName.equals("z")) {
//                    if (!attributes.contains("R=")) {
//                        attributes = attributes + ",R=3";
//                    }
//                }
//                for (String x : attributes.split("(?<!:),")) {
//                    String[] settValues = x.split("=");
//                    if (settValues[0].equals("R")) {
//                        DecimalFormat decimalFormat = new DecimalFormat();
//                        decimalFormat.setMinimumFractionDigits(Integer.parseInt(settValues[1]));
//                        decimalFormat.setMaximumFractionDigits(Integer.parseInt(settValues[1]));
//                        decimalFormat.setGroupingUsed(false); // Removes spacing in format: 1 000 -> 1000
//                        replacement = decimalFormat.format(Float.parseFloat(replacement));
//                    } else if (settValues[0].equals("S")) {
//                        if (Boolean.parseBoolean(settValues[1])) {
//                            replacement = replacement.split(":")[1];
//                        }
//                    }
//                }
//
//                matcher.appendReplacement(result, replacement);
            } catch (Exception e) {
                matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.error").getString());
            }
        }
        matcher.appendTail(result);

        Pattern mathPattern = Pattern.compile("%([\\d+\\-*/^ a-z]*)%");
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