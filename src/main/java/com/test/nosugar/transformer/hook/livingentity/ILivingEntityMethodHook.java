package com.test.nosugar.transformer.hook.livingentity;

import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ILivingEntityMethodHook {
    float getHealth(float original, Object entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isDeadOrDying(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isAlive(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isRemoved(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase);
}