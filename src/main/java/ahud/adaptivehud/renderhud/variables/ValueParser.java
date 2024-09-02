package ahud.adaptivehud.renderhud.variables;

import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultFlag;
import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultFlagCont;
import ahud.adaptivehud.renderhud.variables.annotations.SpecialFlagName;
import com.udojava.evalex.Expression;
import net.minecraft.text.Text;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;
import static ahud.adaptivehud.AdaptiveHUD.variableRegister;

public class ValueParser {
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
        Pattern pattern = Pattern.compile("\\{(\\w+)?((?: *-[a-zA-Z]+(?:=[a-zA-Z0-9._]+)?)*)((?: *--[a-zA-Z]+(?:=[a-zA-Z0-9._]+)?)*)}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);

            try {
                Method method = variableRegister.loadVariable(varName);

                if (method != null) {
                    String globalFlagString = matcher.group(2).replaceAll(" ", "");
                    String flagString = matcher.group(3).replaceAll(" ", ""); // WILL GET ERROR IF NULL?

                    // Global Variables:
                    HashMap<String, String> flags = new HashMap<>();

                    if (!globalFlagString.isEmpty()) {
                        for (String x : globalFlagString.split(" *-")) {
                            if (x.contains("=")) {
                                String[] valueFlag = x.split("=");
                                flags.put(valueFlag[0], valueFlag[1]);
                            } else {
                                flags.put(x, null);
                            }
                        }
                    }

                    if (method.isAnnotationPresent(SetDefaultFlagCont.class)) {
                        for (SetDefaultFlag x : method.getAnnotation(SetDefaultFlagCont.class).value()) {
                            if (!flags.containsKey(x.flag())) {
                                flags.put(x.flag(), x.value().isEmpty() ? null : x.value());
                            }
                        }
                    }

                    HashMap<String, String> localFlags = new HashMap<>();

                    if (flagString != null) {
                        for (String flag : flagString.split("--")) {
                            if (flag.contains("=")) {
                                String[] values = flag.split("=");
                                localFlags.put(values[0], values[1]);
                            } else {
                                localFlags.put(flag, null);
                            }
                        }
                    }

                    Parameter[] params = method.getParameters();
                    Object[] parameters = new Object[params.length];

                    for (int i = 0; i < params.length; i++) {
                        Parameter param = params[i];
                        if (param.isAnnotationPresent(SpecialFlagName.class)) {
                            String keyName = param.getAnnotation(SpecialFlagName.class).value();
                            if (localFlags.containsKey(keyName)) {
                                if (localFlags.get(keyName) != null) {
                                    parameters[i] = localFlags.get(keyName);
                                } else {
                                    parameters[i] = true;
                                }
                            }
                        }
                    }

                    // Call function and parse normal variables
                    String varValue = String.valueOf(method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters));
                    varValue = new FlagParser().parseFlags(varValue, flags);

                    matcher.appendReplacement(result, varValue);
                }
            } catch (Exception e) {
                matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.variable_error").getString());
                e.printStackTrace();
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