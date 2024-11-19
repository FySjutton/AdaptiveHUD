package ahud.adaptivehud.renderhud.element_values.attributes.attribute_classes;

import ahud.adaptivehud.renderhud.element_values.annotations.SetDefaultGlobalFlag;
import net.minecraft.entity.LivingEntity;

public class Entity {
    private final net.minecraft.entity.Entity entity;

    public Entity(net.minecraft.entity.Entity entity) {
        this.entity = entity;
    }

    public String name() {
        return entity.getNameForScoreboard();
    }

    public String entity_type() {
        return entity.getType().getName().getString();
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String x() {
        return String.valueOf(entity.getX());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String y() {
        return String.valueOf(entity.getY());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String z() {
        return String.valueOf(entity.getZ());
    }

    public String uuid() {
        return String.valueOf(entity.getUuid());
    }

    public String glowing() {
        return String.valueOf(entity.isGlowing());
    }

    public String in_fluid() {
        return String.valueOf(entity.isInFluid());
    }

    public String on_rail() {
        return String.valueOf(entity.isOnRail());
    }

    public String on_fire() {
        return String.valueOf(entity.isOnFire());
    }

    public String height() {
        return String.valueOf(entity.getHeight());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String pitch() {
        return String.valueOf(entity.getPitch());
    }

    @SetDefaultGlobalFlag(flag = "round", values = {"1"})
    public String yaw() {
        return String.valueOf(entity.getYaw());
    }

    public String step_height() {
        return String.valueOf(entity.getStepHeight());
    }

    public String attackable() {
        return String.valueOf(entity.isAttackable());
    }

    public net.minecraft.entity.Entity controlled_by() {
        return entity.getControllingPassenger();
    }

    public String health() {
        return String.valueOf(((LivingEntity) entity).getHealth());
    }

    public String max_health() {
        return String.valueOf(((LivingEntity) entity).getMaxHealth());
    }

    public net.minecraft.entity.Entity riding_entity() {
        return entity.getVehicle();
    }

//    public String jump_height() {
//        AbstractHorseEntity entity1 = (AbstractHorseEntity) entity;
//        float jumpStrength = ((HorseStatAccessorMixin) entity1);
//        if (o instanceof HorseStatAccessorMixin horse) {
//        HorseEntity abstractHorseEntity = ((HorseEntity) entity);
//        abstractHorseEntity.ju
//        AbstractHorse.MAX_JUMP_STRENGTH
//        return -0.1817584952 * jumpStrength * jumpStrength * jumpStrength + 3.689713992 * jumpStrength * jumpStrength +
//                2.128599134 * jumpStrength - 0.343930367
////        EntityAttributeInstance attributeInstance = ((LivingEntity) entity).getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
////
////        ((net.minecraft.entity.passive.AbstractHorseEntity) entity).getAttributeInstance();
////        return String.valueOf();
//
//    }
}
