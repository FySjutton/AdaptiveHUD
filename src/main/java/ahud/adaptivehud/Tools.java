package ahud.adaptivehud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class Tools {
    public MutableText colorTextRenderer(String text, int toIndex, Stack<Character> openingChars) {
        MutableText displayText = Text.empty();
        char actualLast = '0';

        char[] chars = text.toCharArray();

        for (int i = 0; i < toIndex; i++) {
            char v = chars[i];
            int color = 0xFFFFFFFF;
            char last = openingChars.get(openingChars.size() - 1);
            String next = String.valueOf(i + 1 < chars.length ? chars[i + 1] : " ");

            if (actualLast != '\\' && v == '{') {
                openingChars.add(v);
                color = 0xffffc766;
            } else if (v == '.' && actualLast != '\\' && (last == '{' || last == '.')) {
                color = 0xff8c8c8c;
                if (last != '.') {
                    openingChars.add('.');
                }
            } else if (v == '-' && actualLast != '\\' && (last == '{' || last == '-' || last == '=' || last == '.')) {
                color = 0xffe942f5;
                if (openingChars.get(openingChars.size() - 1) != '-') {
                    openingChars.add('-');
                }
            } else if (v == '}' && actualLast != '\\' && (last == '{' || last == '-' || last == '=' || last == '.')) {
                color = 0xffffc766;
                int del = openingChars.size() - openingChars.lastIndexOf('{');
                for (int j = 0; j < del; j++) {
                    openingChars.removeLast();
                }
            } else if (last == '-' && v == '=') {
                color = 0xffdcf043;
                openingChars.add('=');
            } else if (last == '=') {
                color = 0xff43f051;
            } else if (last == '-') {
                color = 0xff8f44eb;
            } else if (last == '.') {
                color = 0xff6ba4ff;
            } else if (last == '{') {
                color = 0xff4287f5;
            } else if (actualLast != '\\' && v == '[') {
                openingChars.add(v);
                color = 0xffffc766;
            } else if (last == '[' && (v == '(' || v == ')' || v == '=' || v == '<' || v == '>' || v == '&' || v == '|')) {
                color = 0xff8c8c8c;
            } else if (last == '[' && (String.valueOf(v).matches("\\d") || (v == '-' && next.matches("\\d")) || (v == '.' && String.valueOf(actualLast).matches("\\d") && next.matches("\\d")))) {
                color = 0x6bffd3;
            } else if (last == '[' && v == ':') {
                openingChars.add(v);
                color = 0xff6be1;
            } else if (last == ':' && v != ']' && v != ',') {
                color = 0x6bff8b;
            } else if (last == ':' && v == ',') {
                color = 0xc46bff;
            } else if (last == ':') { // ]
                color = 0xffffc766;
                int del = openingChars.size() - openingChars.lastIndexOf('[');
                for (int j = 0; j < del; j++) {
                    openingChars.removeLast();
                }
            }

            actualLast = v;
            displayText.append(Text.literal(String.valueOf(v)).withColor(color));
        }

        return displayText;
    }

    public Integer parseColor(String colorString) {
        try {
            if (colorString.startsWith("#")) {
                colorString = colorString.substring(1);
            }

            int alpha = 255;
            if (colorString.length() == 8) {
                alpha = Integer.parseInt(colorString.substring(6, 8), 16);
                colorString = colorString.substring(0, 6);
            }

            int red = Integer.parseInt(colorString.substring(0, 2), 16);
            int green = Integer.parseInt(colorString.substring(2, 4), 16);
            int blue = Integer.parseInt(colorString.substring(4, 6), 16);

            return (alpha << 24) | (red << 16) | (green << 8) | blue;
        } catch (Exception e) {
            return null;
        }
    }

    public void sendToast(String title, String description) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.textRenderer != null) {
                client.getToastManager().add(
                        new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
                                Text.literal(title),
                                Text.literal(description)
                        )
                );
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to display toast! Toast title: " + title + ", toast description: " + description + ". Error:");
            LOGGER.error(String.valueOf(e));
        }
    }
}
