package ahud.adaptivehud.renderhud.variables.attributes;

import ahud.adaptivehud.renderhud.variables.annotations.SetDefaultGlobalFlag;
import ahud.adaptivehud.renderhud.variables.attributes.attribute_classes.Item;
import ahud.adaptivehud.renderhud.variables.attributes.attribute_classes.Player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Method;
import java.util.Arrays;

import static ahud.adaptivehud.AdaptiveHUD.LOGGER;

public class AttributeParser {
    public AttributeResult parseAttributes(String[] attributes, Object value) {
        for (int i = 0; i < attributes.length; i++) {
            try {
                Method method;
                if (value instanceof PlayerEntity) {
                    Player playerAttributes = new Player((PlayerEntity) value);
                    method = Player.class.getMethod(attributes[i]);
                    value = method.invoke(playerAttributes);
                } else if (value instanceof ItemStack) {
                    Item server = new Item((ItemStack) value);
                    method = Item.class.getMethod(attributes[i]);
                    value = method.invoke(server);
                } else {
                    return null;
                }

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
