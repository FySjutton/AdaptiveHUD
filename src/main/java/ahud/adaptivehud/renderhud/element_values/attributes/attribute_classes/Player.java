package ahud.adaptivehud.renderhud.element_values.attributes.attribute_classes;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class Player {
    private final PlayerEntity player;

    public Player(ClientPlayerEntity player) {
        this.player = player;
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String x() {
        return String.valueOf(player.getX());
    }

    public ItemStack off_hand() {
        return player.getOffHandStack();
    }
}