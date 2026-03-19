package com.test.nosugar.transformer.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class LivingEntityMethodEvent extends Event {

    private final Entity entity;
    private final MethodType methodType;
    private final MethodPhase phase;
    private Object returnValue;
    private boolean isModified;

    public LivingEntityMethodEvent(Entity entity, MethodType methodType, MethodPhase phase, Object originalReturnValue) {
        this.entity = entity;
        this.methodType = methodType;
        this.phase = phase;
        this.returnValue = originalReturnValue;
        this.isModified = false;
    }

    public LivingEntityMethodEvent(Entity entity, MethodType methodType, Object originalReturnValue) {
        this(entity, methodType, MethodPhase.RETURN, originalReturnValue);
    }

    public Entity getEntity() { return entity; }
    public MethodType getMethodType() { return methodType; }
    public MethodPhase getMethodPhase() { return phase; }
    public Object getReturnValue() { return returnValue; }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
        this.isModified = true;
    }

    public boolean isModified() { return isModified; }
    public void setModified(boolean modified) { isModified = modified; }
    public enum MethodPhase {
        RETURN,
        AFTER
    }

    public enum MethodType {
        GET_HEALTH,
        IS_DEAD_OR_DYING,
        IS_ALIVE,
    }
}