package ahud.adaptivehud.renderhud.element_values.attributes.attribute_classes;

import net.minecraft.item.ItemStack;

public class Item {
    private final ItemStack items;

    public Item (ItemStack items) {
        this.items = items;
    }

    public String count() {
        return String.valueOf(items.getCount());
    }
}