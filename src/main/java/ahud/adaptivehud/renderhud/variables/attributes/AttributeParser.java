package ahud.adaptivehud.renderhud.variables.attributes;

import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlag;
import ahud.adaptivehud.renderhud.variables.attributes.attribute_classes.Item;
import ahud.adaptivehud.renderhud.variables.attributes.attribute_classes.Player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static ahud.adaptivehud.AdaptiveHUD.ATTRIBUTE_CLASSES;
import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class AttributeParser {
    public AttributeResult parseAttributes(String[] attributes, Object value) {
        for (int i = 0; i < attributes.length; i++) {
            try {
                Class<?> customClass = ATTRIBUTE_CLASSES.get(value.getClass());
                if (customClass == null) {
                    return null;
                }

                Method method = customClass.getMethod(attributes[i]);
                Constructor<?> constructor =  customClass.getDeclaredConstructor(value.getClass());
                value = method.invoke(constructor.newInstance(value));

                if (i == attributes.length - 1) {
                    return new AttributeResult(value, method);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
