package com.test.nosugar.utils.entity.hook.livingentity;


import com.test.nosugar.utils.entity.event.LivingEntityMethodEvent;

public interface ILivingEntityMethodHook {
    float getHealth(float original, Object entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isDeadOrDying(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isAlive(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase);

    boolean isRemoved(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase);
}