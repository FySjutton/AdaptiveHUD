package ahud.adaptivehud.renderhud.element_values;

import ahud.adaptivehud.AdaptiveHudRegistry;
import ahud.adaptivehud.renderhud.element_values.annotations.RequiresAttributes;
import ahud.adaptivehud.renderhud.element_values.annotations.LocalFlagName;
import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlagCont;
import ahud.adaptivehud.renderhud.element_values.attributes.AttributeParser;
import ahud.adaptivehud.renderhud.element_values.attributes.AttributeResult;
import com.ezylang.evalex.Expression;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class ValueParser {
    public String parseValue(String text) {
        text = text.replaceAll("&(?=[\\da-fA-Fk-oK-OrR])", "§");

        char[] textList = text.toCharArray();

        Stack<Character> lastCharacters = new Stack<>();
        lastCharacters.push('0');
        Stack<Integer> startPositions = new Stack<>();
        char actualLast = 0;

        Stack<Character> lastNotToUseCharacters = new Stack<>();

//        LOGGER.info("new");
        for (int i = 0; i < text.length(); i++) {
//            LOGGER.info("b");
//            LOGGER.info(String.valueOf(actualLast));
//            LOGGER.info(String.valueOf(textList[i]));
            if (actualLast == '$' && textList[i] == '{') {
//                LOGGER.info("found");
                lastCharacters.pop();
                lastCharacters.push('$');
                startPositions.push(i);
            } else if (lastCharacters.peek() != '$' && (textList[i] == '{' || textList[i] == '[' || (textList[i] == '%' && lastCharacters.peek() != '%')) && actualLast != '\\') {
//                LOGGER.info("ADDING to start");
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
//                LOGGER.info(String.valueOf(lastCharacters.peek()));
                char type = textList[i];
                char lastChar = lastCharacters.peek();
                lastCharacters.pop();

                int startPos = startPositions.pop();
                String innerContent = text.substring(startPos + 1, i);
                String parsedResult = null;
                if (type == '}') {
                    if (lastChar == '{') {
//                        LOGGER.info("looking up var " + innerContent);
                        Object result = parseVariable(innerContent);
                        if (result instanceof String) {
                            parsedResult = String.valueOf(result);
                        } else {
//                            LOGGER.info("error");
                        }
                        if (parsedResult == null) {
                            return Text.translatable("adaptivehud.variable.variable_error").getString();
                        }
                    } else {
//                        LOGGER.info("parsing loop " + innerContent);
                        parsedResult = parseLoop(innerContent);
//                        if (parsedResult == null) {
//                            return "nuhuu";
//                        }
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
                Object varValue = parseVariable(variableString);
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

    private Object parseVariable(String text) {
        Pattern variablePattern = Pattern.compile("(\\w+)((?:\\.\\w+)*)((?: *-[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)((?: *--[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)");
        Matcher matcher = variablePattern.matcher(text);
        if (matcher.matches()) {
            String varName = matcher.group(1);
            try {
                Method method = new AdaptiveHudRegistry().loadVariable(varName);

                if (method != null) {
                    String globalFlagString = matcher.group(3).replaceAll(" ", "");
                    String flagString = matcher.group(4).replaceAll(" ", ""); // WILL GET ERROR IF NULL?

                    // Global Variables:
                    HashMap<String, String[]> flags = new HashMap<>();

                    if (!globalFlagString.isEmpty()) {
                        for (String x : globalFlagString.split("-")) {
                            if (!x.isEmpty()) {
                                if (x.contains("=")) {
                                    String[] valueFlag = x.split("=");
                                    flags.put(valueFlag[0], valueFlag[1].split("(?<!\\\\);"));
                                } else {
                                    flags.put(x, null);
                                }
                            }
                        }
                    }

                    // Local flags (variable specific)
                    HashMap<String, String> localFlags = new HashMap<>();

                    if (!flagString.isEmpty()) {
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
                        if (param.isAnnotationPresent(LocalFlagName.class)) {
                            String keyName = param.getAnnotation(LocalFlagName.class).value();
                            if (localFlags.containsKey(keyName)) {
                                boolean paramBoolean = param.getType() == Boolean.class;
                                if (localFlags.get(keyName) != null) {
                                    parameters[i] = paramBoolean ? false : localFlags.get(keyName);
                                } else {
                                    parameters[i] = paramBoolean ? true : "1";
                                }
                            }
                        }
                    }

                    String varValue;
                    if (!matcher.group(2).isEmpty()) {
                        String[] attributes = matcher.group(2).substring(1).split("\\.");
                        Object result = method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters);
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
                            flags.put(annFlag.flag(), annFlag.values());
                        }
                    } else if (method.isAnnotationPresent(SetDefaultGlobalFlagCont.class)) {
                        for (SetDefaultGlobalFlag x : method.getAnnotation(SetDefaultGlobalFlagCont.class).value()) {
                            if (!flags.containsKey(x.flag())) {
                                flags.put(x.flag(), x.values());
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
        Pattern pattern = Pattern.compile("\" *(.+)\" *for *(\\w+) *in *\\{(\\w+)}(?: *if *\"(\\w+)\"(?: *else *\"(.+)\")*)*");
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();

        if (matcher.matches()) {
            String value = matcher.group(1);
            String varName = matcher.group(2);
            String ofValue = matcher.group(3);
            String ifValue = matcher.group(4);
            String elseValue = matcher.group(5);

            String finishedResult = "";

            Object iterableRes = parseVariable(ofValue);
            if (iterableRes instanceof Iterable<?> iterable) {
                for (Object item : iterable) {
                    String processedValue = value;

                    Pattern attribute_finder = Pattern.compile("\\{" + varName + "\\.([.\\w]+)}");
                    Matcher attribute_matcher = attribute_finder.matcher(processedValue);
                    StringBuilder entityBuilder = new StringBuilder();

                    while (attribute_matcher.find()) {
                        String attributes = attribute_matcher.group(1);
                        AttributeResult replaceValue = new AttributeParser().parseAttributes(attributes.split("\\."), item);

                        String replacement = (replaceValue != null && replaceValue.value() != null)
                                ? String.valueOf(replaceValue.value())
                                : "null";

                        attribute_matcher.appendReplacement(entityBuilder, Matcher.quoteReplacement(replacement));
                    }

                    attribute_matcher.appendTail(entityBuilder);
                    builder.append(entityBuilder);
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
            return new Expression(expression).evaluate().getBooleanValue();
        } catch (Exception e ) {
            return false;
        }
    }

    private BigDecimal numberExpression(String value) {
        try {
            return new Expression(value).evaluate().getNumberValue();
        } catch (Exception e ) {
            return BigDecimal.valueOf(0);
        }
    }
}