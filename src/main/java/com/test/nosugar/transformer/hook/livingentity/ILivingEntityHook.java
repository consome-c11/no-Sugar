package com.test.nosugar.transformer.hook.livingentity;

import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ILivingEntityHook {
    float getHealth(float original, LivingEntity entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isDeadOrDying(boolean original, LivingEntity entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isAlive(boolean original, Entity entity, LivingEntityMethodEvent.MethodPhase phase);
}