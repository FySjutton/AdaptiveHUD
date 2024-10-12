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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class Tools {

    public int colorTextRenderer(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, Stack<Character> startPositions) {
        MutableText displayText = Text.empty();

//        LOGGER.info(text);
         // to avoid indexoutofbounds

        for (char v : text.toCharArray()) {
            int color = 0xFFFFFF;
            char last = startPositions.get(startPositions.size() - 1);
            if (v == '{') {
                startPositions.add(v);
                color = 0xffffc766;
            } else if (v == '-' && (last == '{' || last == '-' || last == '=')) {
                color = 0xffe942f5;
                if (startPositions.get(startPositions.size() - 1) != '-') {
                    startPositions.add('-');
                }
            } else if (v == '}' && (last == '{' || last == '-' || last == '=')) {
                color = 0xffffc766;
                startPositions.remove(startPositions.size() - 1);
            } else if (last == '-' && v == '=') {
                color = 0xffdcf043;
                startPositions.add('=');
            } else if (last == '=') {
                color = 0xff43f051;
            } else if (last == '-') {
                color = 0xff8f44eb;
            } else if (last == '{') {
                color = 0xff4287f5;
            }

            displayText.append(Text.literal(String.valueOf(v)).withColor(color));
        }

//        String[] parts = text.split("(?<=\\W)|(?=\\W)");

//        for (String part : parts) {
//            int color = 0xFFFFFFFF;
//
//            if (part.equals("{") ||part.equals("}")) {
//                color = 0xffffc766;
//            }
//
//            displayText.append(Text.literal(part).withColor(color));
//
//        }

        return instance.drawTextWithShadow(textRenderer, displayText, x, y, 0xFFFFFFFF);
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
