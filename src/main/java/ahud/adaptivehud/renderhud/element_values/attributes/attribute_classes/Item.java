package ahud.adaptivehud.renderhud.element_values.attributes.attribute_classes;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class Item {
    private final ItemStack items;

    public Item (ItemStack items) {
        this.items = items;
    }

    public String name() {
        return String.valueOf(items.getName().getString());
    }

    public String count() {
        return String.valueOf(items.getCount());
    }

    public String item_name() {
        return items.getRegistryEntry().getKey().get().getValue().toString();
    }

    public String damage() {
        return String.valueOf(items.getDamage());
    }

    public String damaged() {
        return String.valueOf(items.isDamaged());
    }

    public String max_damage() {
        return String.valueOf(items.getMaxDamage());
    }

    public String rarity() {
        return String.valueOf(items.getRarity().asString());
    }

    public String enchanted() {
        return String.valueOf(items.hasEnchantments());
    }

    public String glint() {
        return String.valueOf(items.hasGlint());
    }

    public String stackable() {
        return String.valueOf(items.isStackable());
    }
    public String max_count() {
        return String.valueOf(items.getMaxCount());
    }
    public String damageable() {
        return String.valueOf(items.isDamageable());
    }

    public Entity holder() {
        return items.getHolder();
    }
}