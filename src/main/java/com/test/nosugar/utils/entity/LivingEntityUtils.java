package com.test.nosugar.utils.entity;

import net.minecraft.world.entity.LivingEntity;

//wrappingさせるため
public class LivingEntityUtils {
    public static float getHealth(LivingEntity living) {
        return living.getHealth();
    }

    public static boolean isAlive(LivingEntity living) {
        return living.isAlive();
    }

    public static boolean isDeadOrDying(LivingEntity living) {
        return living.isDeadOrDying();
    }

    public static boolean isRemoved(LivingEntity living) {
        return living.isRemoved();
    }
}
