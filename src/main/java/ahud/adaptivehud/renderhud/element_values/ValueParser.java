package ahud.adaptivehud.renderhud.element_values;

import ahud.adaptivehud.AdaptiveHudRegistry;
import ahud.adaptivehud.renderhud.element_values.annotations.RequiresAttributes;
import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlagCont;
import ahud.adaptivehud.renderhud.element_values.attributes.AttributeParser;
import ahud.adaptivehud.renderhud.element_values.attributes.AttributeResult;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class ValueParser {
    public String parseValue(String text, String loopVarName, Object loopValue) {
        text = text.replaceAll("&(?=[\\da-fA-Fk-oK-OrR])", "§");

        char[] textList = text.toCharArray();

        Stack<Character> lastCharacters = new Stack<>();
        lastCharacters.push('0');
        Stack<Integer> startPositions = new Stack<>();
        char actualLast = 0;

        Stack<Character> lastNotToUseCharacters = new Stack<>();

        for (int i = 0; i < text.length(); i++) {
            if (actualLast == '$' && textList[i] == '{') {
                lastCharacters.pop();
                lastCharacters.push('$');
                startPositions.push(i);
            } else if (lastCharacters.peek() != '$' && (textList[i] == '{' || textList[i] == '[' || (textList[i] == '%' && lastCharacters.peek() != '%')) && actualLast != '\\') {
                startPositions.push(i);
                lastCharacters.push(textList[i]);
            } else if (lastCharacters.peek() == '$' && textList[i] == '{') {
                lastNotToUseCharacters.push('{');
            } else if (lastCharacters.peek() == '$' && textList[i] == '}' && !lastNotToUseCharacters.isEmpty()) {
                lastNotToUseCharacters.pop();
            } else if (((textList[i] == '}' && lastCharacters.peek() == '{') ||
                    (textList[i] == ']' && lastCharacters.peek() == '[') ||
                    (textList[i] == '}' && lastCharacters.peek() == '$') ||
                    (textList[i] == '%')) && actualLast != '\\') {
                char type = textList[i];
                char lastChar = lastCharacters.peek();
                lastCharacters.pop();

                int startPos = startPositions.pop();
                String innerContent = text.substring(startPos + 1, i);
                String parsedResult = null;
                if (type == '}') {
                    if (lastChar == '{') {
                        Object result = parseVariable(innerContent, loopVarName, loopValue);
                        if (result instanceof String) {
                            parsedResult = String.valueOf(result);
                        }
                        if (parsedResult == null) {
                            return Text.translatable("adaptivehud.variable.variable_error").getString();
                        }
                    } else {
                        parsedResult = parseLoop(innerContent);
                    }

                } else if (type == ']') {
                    parsedResult = parseCondition(innerContent);
                    if (parsedResult == null) {
                        return Text.translatable("adaptivehud.variable.condition_error").getString();
                    }
                } else {
                    parsedResult = parseMath(innerContent);
                    if (parsedResult == null) {
                        return Text.translatable("adaptivehud.variable.math_error").getString();
                    }
                }

                text = text.substring(0, startPos) + parsedResult + text.substring(i + 1);
                textList = text.toCharArray();

                i = startPos + parsedResult.length() - 1;
            }
            if (textList.length > 0) {
                actualLast = textList[i];
            }
        }

        return text;
    }

    public boolean renderCheck(String text) {
        try {
            Pattern pattern = Pattern.compile("\\{(\\w+)((?:\\.\\w+)*)((?: *-[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)((?: *--[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)}");
            Matcher matcher = pattern.matcher(text);
            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                String variableString = matcher.group(1);
                Object varValue = parseVariable(variableString, null, null);
                if (varValue instanceof String) {
                    matcher.appendReplacement(result, String.valueOf(varValue)); // wont be null cuz of the regex above
                }
            }
            matcher.appendTail(result);

            return parseBooleanExpression(result.toString());
        } catch (Exception e) {
            return false;
        }
    }

    private Object parseVariable(String text, String loopVarName, Object loopValue) {
        Pattern variablePattern = Pattern.compile("(\\w+)((?:\\.\\w+)*)((?: *- *[a-zA-Z]+(?: *= *\"(?:[^\\-\\\\]|\\\\.)+\")?)*)((?: *-- *[a-zA-Z]+(?: *= *\"(?:[^\\-\\\\]|\\\\.)+\")?)*)");
        Matcher matcher = variablePattern.matcher(text);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            try {
                Method method = new AdaptiveHudRegistry().loadVariable(varName);

                if (method != null) {
                    String globalFlagString = matcher.group(3).replaceAll(" ", "");
                    String flagString = matcher.group(4).replaceAll(" ", ""); // WILL GET ERROR IF NULL?

                    // Global Variables:
                    HashMap<String, List<String>> flags = new HashMap<>();

                    if (!globalFlagString.isEmpty()) {
                        for (String x : globalFlagString.split("-")) {
                            if (!x.replace(" ", "").isEmpty()) {
                                if (x.contains("=")) {
                                    String[] valueFlag = x.split("=");
                                    List<String> values = Arrays.stream(valueFlag[1].split("(?<!\\\\);")).map(
                                            val -> val.substring(val.indexOf("\"") + 1, val.lastIndexOf("\""))
                                    ).toList();
                                    flags.put(valueFlag[0], values);
                                } else {
                                    flags.put(x.replace(" ", ""), null);
                                }
                            }
                        }
                    }

                    // Local flags (variable specific)
                    HashMap<String, List<String>> localFlags = new HashMap<>();

                    if (!flagString.isEmpty()) {
                        for (String flag : flagString.split("--")) {
                            if (!flag.replace(" ", "").isEmpty()) {
                                if (flag.contains("=")) {
                                    String[] values = flag.split("=");
                                    List<String> mappedVals = Arrays.stream(values[1].split("(?<!\\\\);")).map(
                                            val -> val.substring(val.indexOf("\"") + 1, val.lastIndexOf("\""))
                                    ).toList();
                                    localFlags.put(values[0], mappedVals);
                                } else {
                                    localFlags.put(flag.replace(" ", ""), null);
                                }
                            }
                        }
                    }

                    Parameter[] params = method.getParameters();
                    Object[] parameters = new Object[params.length];

                    for (int i = 0; i < params.length; i++) {
                        Parameter param = params[i];
                        if (param.isAnnotationPresent(LocalFlagName.class)) {
                            String keyName = param.getAnnotation(LocalFlagName.class).value();
                            if (localFlags.containsKey(keyName)) {
                                parameters[i] = localFlags.get(keyName);
                            }
                        }
                    }

                    String varValue;
                    if (!matcher.group(2).isEmpty()) {
                        String[] attributes = matcher.group(2).substring(1).split("\\.");
                        Object result;
                        if (loopVarName != null) {
                            result = loopValue;
                        } else {
                            result = method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters);
                        }
                        AttributeResult parsedResult = new AttributeParser().parseAttributes(attributes, result);
                        method = parsedResult.method();
                        Object resultText = parsedResult.value();
                        if (resultText instanceof String) {
                            varValue = String.valueOf(resultText);
                        } else {
                            return resultText;
                        }
                    } else if (!method.isAnnotationPresent(RequiresAttributes.class)) {
                        Object value = method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters);
                        if (value instanceof String) {
                            varValue = String.valueOf(value);
                        } else {
                            return value;
                        }
                    } else {
                        return null;
                    }

                    if (method.isAnnotationPresent(SetDefaultGlobalFlag.class)) {
                        SetDefaultGlobalFlag annFlag = method.getAnnotation(SetDefaultGlobalFlag.class);
                        if (!flags.containsKey(annFlag.flag())) {
                            flags.put(annFlag.flag(), Arrays.stream(annFlag.values()).toList());
                        }
                    } else if (method.isAnnotationPresent(SetDefaultGlobalFlagCont.class)) {
                        for (SetDefaultGlobalFlag x : method.getAnnotation(SetDefaultGlobalFlagCont.class).value()) {
                            if (!flags.containsKey(x.flag())) {
                                flags.put(x.flag(), Arrays.stream(x.values()).toList());
                            }
                        }
                    }

                    // Call function and parse normal variables
                    varValue = new FlagParser().parseFlags(varValue, flags);
                    return varValue;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String parseLoop(String text) {
        Pattern pattern = Pattern.compile("\" *(.+)\" *for *(\\w+) *in *\\{(\\w+)}(?: *if *\"([^\"]+)\"(?: *else *\"(.+)\")*)*");
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();

        if (matcher.matches()) {
            String value = matcher.group(1);
            String varName = matcher.group(2);
            String ofValue = matcher.group(3);
            String ifValue = matcher.group(4);
            String elseValue = matcher.group(5);

            Object iterableRes = parseVariable(ofValue, null, null);
            if (iterableRes instanceof Iterable<?> iterable) {
                for (Object obj : iterable) {
                    String responseValue = "";
                    boolean useValue = true;
                    if (ifValue != null) {
                        String parsedIfValue = parseValue(ifValue, varName, obj);
//                        LOGGER.info(parsedIfValue);
                        useValue = parseBooleanExpression(parsedIfValue);
//                        LOGGER.info(String.valueOf(useValue));
                    }
                    if (useValue) {
                        responseValue = parseValue(value, varName, obj);
                    } else if (elseValue != null) {
                        responseValue = parseValue(elseValue, varName, obj);
                    }
                    builder.append(responseValue);
                }
            }
        }
        return builder.toString();
    }

    private String parseCondition(String text) {
        Pattern conditionPattern = Pattern.compile("((?:[^:,\\\\]|\\\\.)+): *\"((?:[^\"\\\\]|\\\\.)+)\" *((?:,(?:[^:,\\\\]|\\\\.)+: *\"(?:[^\":,\\\\]|\\\\.)+\")*)(?: *, *\"((?:[^\"\\\\]|\\\\.)+)\")?");
        Matcher matcher = conditionPattern.matcher(text);

        if (matcher.matches()) {
            try {
                String ifCondition = matcher.group(1);
                String ifValue = matcher.group(2);
                String elseIfs = matcher.group(3);
                String elseValue = matcher.group(4);
                if (elseValue == null) {
                    elseValue = "";
                }

                if (parseBooleanExpression(ifCondition)) {
                    return ifValue;
                } else {
                    if (!elseIfs.isEmpty()) {
                        for (String x : elseIfs.substring(1).split("(?<!\\\\),")) {
                            String[] conVal = x.split("(?<!\\\\):");
                            if (parseBooleanExpression(conVal[0])) {
                                return conVal[1].split("(?<!\\\\)\"")[1];
                            }
                        }
                    }

                    return elseValue;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String parseMath(String text) {
        try {
            return numberExpression(text).toPlainString();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean parseBooleanExpression(String expression) {
        try {
            ExpressionConfiguration configuration = ExpressionConfiguration.builder().singleQuoteStringLiteralsAllowed(true).build();

            return new Expression(expression, configuration).evaluate().getBooleanValue();
        } catch (Exception e ) {
            return false;
        }
    }

    private BigDecimal numberExpression(String value) {
        try {
            ExpressionConfiguration configuration = ExpressionConfiguration.builder().singleQuoteStringLiteralsAllowed(true).build();
            return new Expression(value, configuration).evaluate().getNumberValue();
        } catch (Exception e ) {
            return BigDecimal.valueOf(0);
        }
    }
}