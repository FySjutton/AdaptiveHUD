package ahud.adaptivehud.renderhud.variables;

import ahud.adaptivehud.AdaptiveHudRegistry;
import ahud.adaptivehud.renderhud.variables.annotations.RequiresAttributes;
import ahud.adaptivehud.renderhud.variables.annotations.LocalFlagName;
import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlag;
import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlagCont;
import ahud.adaptivehud.renderhud.variables.attributes.AttributeParser;
import ahud.adaptivehud.renderhud.variables.attributes.AttributeResult;
import com.udojava.evalex.Expression;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.HashMap;
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

        for (int i = 0; i < text.length(); i++) {
            if (lastCharacters.peek() != '$' && (textList[i] == '{' || textList[i] == '[' || (textList[i] == '%' && lastCharacters.peek() != '%')) && actualLast != '\\') {
                startPositions.push(i);
                lastCharacters.push(textList[i]);
            }
            else if (actualLast == '{' && textList[i] == '$') {
                lastCharacters.pop();
                lastCharacters.push('$');
            }
            else if (((textList[i] == '}' && lastCharacters.peek() == '{') ||
                    (textList[i] == ']' && lastCharacters.peek() == '[') ||
                    (textList[i] == '}' && lastCharacters.peek() == '$') ||
                    (textList[i] == '%')) && actualLast != '\\') {
                char type = textList[i];
                char lastChar = lastCharacters.peek();
                lastCharacters.pop();

                int startPos = startPositions.pop();
                String innerContent = text.substring(startPos + 1, i);
                String parsedResult;
                if (type == '}') {
                    if (lastChar == '{') {
                        parsedResult = parseVariable(innerContent);
                        if (parsedResult == null) {
                            return Text.translatable("adaptivehud.variable.variable_error").getString();
                        }
                    } else {
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

    public int renderCheck(String text) {
        try {
            Pattern pattern = Pattern.compile("\\{(\\w+)((?:\\.\\w+)*)((?: *-[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)((?: *--[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)}");
            Matcher matcher = pattern.matcher(text);
            StringBuilder result = new StringBuilder();

            while (matcher.find()) {
                String variableString = matcher.group(1);
                String varValue = parseVariable(variableString);
                matcher.appendReplacement(result, varValue); // wont be null cuz of the regex above
            }
            matcher.appendTail(result);

            return !parseBooleanExpression(result.toString()) ? 0 : 1;
        } catch (Exception e) {
            return -1;
        }
    }

    private String parseVariable(String text) {
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
                            return null;
                        }
                    } else if (!method.isAnnotationPresent(RequiresAttributes.class)) {
                        varValue = String.valueOf(method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters));
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
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private String parseLoop(String text) {
        Pattern pattern = Pattern.compile("\\$\"(.+)\" for ([a-z]+) of (\\w+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String value = matcher.group(0);
            String varName = matcher.group(1);
            String ofValue = matcher.group(2);


        }
        return text;
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
            Expression exp = new Expression(text);
            BigDecimal replacement = exp.eval();
            return replacement.toPlainString();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean parseBooleanExpression(String expression) {
        Pattern stringPattern = Pattern.compile("\"([^\"]+)\"(?: *== *\"([^\"]+)\")?");
        Matcher stringMatcher = stringPattern.matcher(expression.replaceAll("(\"null\"|\"false\"|\"Empty\")", "false"));
        StringBuilder stringResult = new StringBuilder();
        while (stringMatcher.find()) {
            if (stringMatcher.group(2) != null) {
                stringMatcher.appendReplacement(stringResult, String.valueOf(stringMatcher.group(1).equals(stringMatcher.group(2))));
            } else {
                stringMatcher.appendReplacement(stringResult, String.valueOf(!stringMatcher.group(1).isEmpty()));
            }
        }
        stringMatcher.appendTail(stringResult);
        Expression expression_fr = new Expression(stringResult.toString());
        return expression_fr.eval().intValue() != 0;
    }
}