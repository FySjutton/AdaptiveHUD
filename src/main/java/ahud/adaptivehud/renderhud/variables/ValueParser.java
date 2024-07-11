package ahud.adaptivehud.renderhud.variables;

import com.udojava.evalex.Expression;
import net.minecraft.text.Text;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueParser {
    VariableRegisterer register = new VariableRegisterer();

    public String parseValue(String text) {
        text = text.replaceAll("&(?=[\\da-fA-Fk-oK-OrR])", "§");

        text = parseVariables(text);
        text = parseConditions(text);
        text = parseMath(text);

        return text;
    }

    private String parseConditions(String text) {
        Pattern pattern = Pattern.compile("\\[([^,:\\[\\]]+):((?:\\\\[,:\\[\\]]|[^,:\\[\\]])+)((?:,[^,:\\[\\]]+:(?:\\\\[,:\\[\\]]|[^,:\\[\\]])+)*)(?:,((?:\\\\[,:\\[\\]]|[^,:\\[\\]])+))?]");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            try {
                String ifCondition = matcher.group(1);
                String ifValue = matcher.group(2);
                String elseIfs = matcher.group(3);
                String elseValue = matcher.group(4);

                if (elseValue == null) {
                    elseValue = "";
                }

                Expression expression = new Expression(ifCondition);
                if (expression.eval().intValue() != 0) {
                    matcher.appendReplacement(result, ifValue.replaceAll("\\\\(?=[\\[\\]:,])", ""));
                } else {
                    boolean found = false;
                    if (!elseIfs.isEmpty()) {
                        for (String x : elseIfs.substring(1).split("(?<!\\\\),")) {
                            String[] conVal = x.split("(?<!\\\\):");
                            Expression exp = new Expression(conVal[0]);
                            if (exp.eval().intValue() != 0) {
                                matcher.appendReplacement(result, conVal[1].replaceAll("\\\\(?=[\\[\\]:,])", ""));
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        matcher.appendReplacement(result, elseValue.replaceAll("\\\\(?=[\\[\\]:,])", ""));
                    }
                }
            } catch (Exception e) {
            matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.condition_error").getString());
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String parseVariables(String text) {
        Pattern pattern = Pattern.compile("\\{(\\w+)?((?: *-[a-z]+)*)((?: *--[a-zA-Z]+=[a-zA-Z0-9.]+)*)}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);

            try {
                Method method = register.loadVariable(varName);

                if (method != null) {
                    String flagString = matcher.group(2);
                    String attributeString = matcher.group(3);

                    Parameter[] params = method.getParameters();
                    Object[] parameters = new Object[params.length];
                    for (int i = 0; i < params.length; i++) {
                        Parameter param = params[i];
                        String paramName = ((AttributeName) param.getAnnotations()[0]).value();
                        Object paramValue = null;

                        if (attributeString != null) {
                            for (String x : attributeString.split("--")) {
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
            } catch (Exception e) {
                matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.variable_error").getString());
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String parseMath(String text) {
        Pattern mathPattern = Pattern.compile("%([\\d+\\-*/^ a-z]*)%");
        Matcher mathMatcher = mathPattern.matcher(text);
        StringBuffer mathResult = new StringBuffer();
        while (mathMatcher.find()) {
            try {
                Expression exp = new Expression(mathMatcher.group(1));
                BigDecimal replacement = exp.eval();
                mathMatcher.appendReplacement(mathResult, String.valueOf(replacement));
            } catch (Exception e) {
                mathMatcher.appendReplacement(mathResult, Text.translatable("adaptivehud.variable.math_error").getString());
            }
        }
        mathMatcher.appendTail(mathResult);
        return mathResult.toString();
    }
}