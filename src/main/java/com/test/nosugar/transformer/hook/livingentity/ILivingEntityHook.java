package com.test.nosugar.transformer.hook.livingentity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ILivingEntityHook {
    float getHealth(float original, LivingEntity entity);
    boolean isDeadOrDying(boolean original, LivingEntity entity);
    boolean isAlive(boolean original, Entity entity);
}