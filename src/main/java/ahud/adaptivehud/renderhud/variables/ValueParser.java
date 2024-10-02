package ahud.adaptivehud.renderhud.variables;

import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlag;
import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlagCont;
import ahud.adaptivehud.renderhud.variables.annotations.SpecialFlagName;
import com.udojava.evalex.Expression;
import net.minecraft.text.Text;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;
import static ahud.adaptivehud.AdaptiveHUD.variableRegister;

public class ValueParser {
    public String parseValue(String text) {
        text = text.replaceAll("&(?=[\\da-fA-Fk-oK-OrR])", "§");

        char[] textList = text.toCharArray();

        Stack<Character> lastCharacters = new Stack<>();
        lastCharacters.push('0');
        Stack<Integer> startPositions = new Stack<>();
        char actualLast = 0;
        char lastOpener = 0;

        for (int i = 0; i < text.length(); i++) {
            if ((textList[i] == '{' || textList[i] == '[' || textList[i] == '%') && actualLast != '\\' && lastOpener != '%') {
                lastOpener = textList[i];
                startPositions.push(i);
                lastCharacters.push(textList[i]);
            }
            else if (((textList[i] == '}' && lastCharacters.peek() == '{') ||
                    (textList[i] == ']' && lastCharacters.peek() == '[') ||
                    (textList[i] == '%')) && actualLast != '\\') {
                char type = textList[i];
                lastCharacters.pop();

                int startPos = startPositions.pop();
                String innerContent = text.substring(startPos + 1, i);
                String parsedResult;
                if (type == '}') {
                    parsedResult = parseVariable(innerContent);
                    if (parsedResult == null) {
                        return Text.translatable("adaptivehud.variable.variable_error").getString();
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
                lastOpener = 0;
            }
            if (textList.length > 0) {
                actualLast = textList[i];
            }
        }

        return text;
    }

    public int renderCheck(String text) {
        try {

            // FIX THIS HAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//            text = parseVariables(text);
            return !parseBooleanExpression(text) ? 0 : 1;
        } catch (Exception e) {
            return -1;
        }
    }

    private String parseVariable(String text) {
        Pattern variablePattern = Pattern.compile("(\\w+)+((?: *-[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)((?: *--[a-zA-Z]+(?:=(?:[^\\-\\\\]|\\\\.)+)?)*)");
        Matcher matcher = variablePattern.matcher(text);
        if (matcher.matches()) {
            String varName = matcher.group(1);

            try {
                Method method = variableRegister.loadVariable(varName);

                if (method != null) {
                    String globalFlagString = matcher.group(2).replaceAll(" ", "");
                    String flagString = matcher.group(3).replaceAll(" ", ""); // WILL GET ERROR IF NULL?

                    // Global Variables:
                    HashMap<String, String[]> flags = new HashMap<>();

                    if (!globalFlagString.isEmpty()) {
                        for (String x : globalFlagString.split(" *-")) {
                            if (x.contains("=")) {
                                String[] valueFlag = x.split("=");
                                flags.put(valueFlag[0], valueFlag[1].split("(?<!\\\\);"));
                            } else {
                                flags.put(x, null);
                            }
                        }
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
                        if (param.isAnnotationPresent(SpecialFlagName.class)) {
                            String keyName = param.getAnnotation(SpecialFlagName.class).value();
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

                    // Call function and parse normal variables
                    String varValue = String.valueOf(method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters));
                    varValue = new FlagParser().parseFlags(varValue, flags);
                    return varValue;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String parseCondition(String text) {
        Pattern conditionPattern = Pattern.compile("((?:[^:,\\\\]|\\\\.)+):((?:[^:,\\\\]|\\\\.)+)((?:,(?:[^:,\\\\]|\\\\.)+:(?:[^:,\\\\]|\\\\.)+)*)(?:,((?:[^:,\\\\]|\\\\.)+))?");
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
                    return ifValue.replaceAll("\\\\n", "\\\\\\\\n");
                } else {
                    if (!elseIfs.isEmpty()) {
                        for (String x : elseIfs.substring(1).split("(?<!\\\\),")) {
                            String[] conVal = x.split("(?<!\\\\):");
                            if (parseBooleanExpression(conVal[0])) {
                                return conVal[1].replaceAll("\\\\n", "\\\\\\\\n");
                            }
                        }
                    }

                    return elseValue.replaceAll("\\\\n", "\\\\\\\\n");
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String parseMath(String text) {
        Pattern mathPattern = Pattern.compile("([\\d+\\-*/^ a-z]*)");
        Matcher matcher = mathPattern.matcher(text);
        if (matcher.matches()) {
            try {
                Expression exp = new Expression(matcher.group(1));
                BigDecimal replacement = exp.eval();
                return replacement.toString();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

//    private String parseConditions(String text) {
//        Pattern pattern = Pattern.compile("\\[((?:[^:\\[\\],\\\\]|\\\\.)+):((?:[^:\\[\\],\\\\]|\\\\.)+)((?:,(?:[^:\\[\\],\\\\]|\\\\.)+:(?:[^:\\[\\],\\\\]|\\\\.)+)*)(?:,((?:[^:\\[\\],\\\\]|\\\\.)+))?]");
//        Matcher matcher = pattern.matcher(text);
//        StringBuilder result = new StringBuilder();
//
//        while (matcher.find()) {
//            try {
//                String ifCondition = matcher.group(1);
//                String ifValue = matcher.group(2);
//                String elseIfs = matcher.group(3);
//                String elseValue = matcher.group(4);
//                if (elseValue == null) {
//                    elseValue = "";
//                }
//
//                if (parseBooleanExpression(ifCondition)) {
//                    matcher.appendReplacement(result, ifValue.replaceAll("\\\\n", "\\\\\\\\n"));
//                } else {
//                    boolean found = false;
//                    if (!elseIfs.isEmpty()) {
//                        for (String x : elseIfs.substring(1).split("(?<!\\\\),")) {
//                            String[] conVal = x.split("(?<!\\\\):");
//                            if (parseBooleanExpression(conVal[0])) {
//                                matcher.appendReplacement(result, conVal[1].replaceAll("\\\\n", "\\\\\\\\n"));
//                                found = true;
//                                break;
//                            }
//                        }
//                    }
//
//                    if (!found) {
//                        matcher.appendReplacement(result, elseValue.replaceAll("\\\\n", "\\\\\\\\n"));
//                    }
//                }
//            } catch (Exception e) {
//                matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.condition_error").getString());
////                e.printStackTrace();
//            }
//        }
//        matcher.appendTail(result);
//        return result.toString();
//    }
//
//    private String parseVariables(String text) {
//        Pattern pattern = Pattern.compile("\\{(\\w+)+((?: *-[a-zA-Z]+(?:=(?:[^:{}=\\-\\\\]|\\\\.)+)?)*)((?: *--[a-zA-Z]+(?:=(?:[^:{}=\\-\\\\]|\\\\.)+)?)*)}");
//        Matcher matcher = pattern.matcher(text);
//        StringBuilder result = new StringBuilder();
//
//        while (matcher.find()) {
//            String varName = matcher.group(1);
//
//            try {
//                Method method = variableRegister.loadVariable(varName);
//
//                if (method != null) {
//                    String globalFlagString = matcher.group(2).replaceAll(" ", "");
//                    String flagString = matcher.group(3).replaceAll(" ", ""); // WILL GET ERROR IF NULL?
//
//                    // Global Variables:
//                    HashMap<String, String[]> flags = new HashMap<>();
//
//                    if (!globalFlagString.isEmpty()) {
//                        for (String x : globalFlagString.split(" *-")) {
//                            if (x.contains("=")) {
//                                String[] valueFlag = x.split("=");
//                                flags.put(valueFlag[0], valueFlag[1].split("(?<!\\\\);"));
//                            } else {
//                                flags.put(x, null);
//                            }
//                        }
//                    }
//
//                    if (method.isAnnotationPresent(SetDefaultGlobalFlag.class)) {
//                        SetDefaultGlobalFlag annFlag = method.getAnnotation(SetDefaultGlobalFlag.class);
//                        if (!flags.containsKey(annFlag.flag())) {
//                            flags.put(annFlag.flag(), annFlag.values());
//                        }
//                    } else if (method.isAnnotationPresent(SetDefaultGlobalFlagCont.class)) {
//                        for (SetDefaultGlobalFlag x : method.getAnnotation(SetDefaultGlobalFlagCont.class).value()) {
//                            if (!flags.containsKey(x.flag())) {
//                                flags.put(x.flag(), x.values());
//                            }
//                        }
//                    }
//
//                    // Local flags (variable specific)
//                    HashMap<String, String> localFlags = new HashMap<>();
//
//                    if (!flagString.isEmpty()) {
//                        for (String flag : flagString.split("--")) {
//                            if (flag.contains("=")) {
//                                String[] values = flag.split("=");
//                                localFlags.put(values[0], values[1]);
//                            } else {
//                                localFlags.put(flag, null);
//                            }
//                        }
//                    }
//
//                    Parameter[] params = method.getParameters();
//                    Object[] parameters = new Object[params.length];
//
//                    for (int i = 0; i < params.length; i++) {
//                        Parameter param = params[i];
//                        if (param.isAnnotationPresent(SpecialFlagName.class)) {
//                            String keyName = param.getAnnotation(SpecialFlagName.class).value();
//                            if (localFlags.containsKey(keyName)) {
//                                boolean paramBoolean = param.getType() == Boolean.class;
//                                if (localFlags.get(keyName) != null) {
//                                    parameters[i] = paramBoolean ? false : localFlags.get(keyName);
//                                } else {
//                                    parameters[i] = paramBoolean ? true : "1";
//                                }
//                            }
//                        }
//                    }
//
//                    // Call function and parse normal variables
//                    String varValue = String.valueOf(method.invoke(method.getDeclaringClass().getDeclaredConstructor().newInstance(), parameters));
////                    LOGGER.info(String.valueOf(flags));
//                    varValue = new FlagParser().parseFlags(varValue, flags);
//
//                    matcher.appendReplacement(result, varValue);
//                }
//            } catch (Exception e) {
////                matcher.appendReplacement(result, e.toString());
////                e.printStackTrace();
//                matcher.appendReplacement(result, Text.translatable("adaptivehud.variable.variable_error").getString());
//            }
//        }
//        matcher.appendTail(result);
//        return result.toString();
//    }
//
//    private String parseMath(String text) {
//        Pattern mathPattern = Pattern.compile("%([\\d+\\-*/^ a-z]*)%");
//        Matcher mathMatcher = mathPattern.matcher(text);
//        StringBuilder mathResult = new StringBuilder();
//        while (mathMatcher.find()) {
//            try {
//                Expression exp = new Expression(mathMatcher.group(1));
//                BigDecimal replacement = exp.eval();
//                mathMatcher.appendReplacement(mathResult, String.valueOf(replacement));
//            } catch (Exception e) {
//                mathMatcher.appendReplacement(mathResult, Text.translatable("adaptivehud.variable.math_error").getString());
//            }
//        }
//        mathMatcher.appendTail(mathResult);
//        return mathResult.toString();
//    }

    private boolean parseBooleanExpression(String expression) {
//        LOGGER.info(expression);
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