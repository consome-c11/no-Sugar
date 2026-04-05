package com.test.nosugar.utils.entity.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class LivingEntityMethodEvent extends Event {

    private final Object entity;
    private final MethodType methodType;
    private final MethodPhase phase;
    private Object returnValue;
    private boolean isModified;
    private boolean forwarded = false;

    public LivingEntityMethodEvent() {
        this.entity = null;
        this.methodType = null;
        this.phase = MethodPhase.RETURN;
        this.returnValue = null;
        this.isModified = false;
    }

    public LivingEntityMethodEvent(LivingEntityMethodEvent oldEvent) {
        this.entity = oldEvent.getEntity();
        this.methodType = oldEvent.getMethodType();
        this.phase = oldEvent.getMethodPhase();
        this.returnValue = oldEvent.getReturnValue();
        this.isModified = oldEvent.isModified();
    }

    public LivingEntityMethodEvent(Object entity, MethodType methodType, MethodPhase phase, Object originalReturnValue) {
        this.entity = entity;
        this.methodType = methodType;
        this.phase = phase;
        this.returnValue = originalReturnValue;
        this.isModified = false;
    }

    public LivingEntityMethodEvent(Object entity, MethodType methodType, Object originalReturnValue) {
        this(entity, methodType, MethodPhase.RETURN, originalReturnValue);
    }

    public Object getEntity() { return entity; }
    public MethodType getMethodType() { return methodType; }
    public MethodPhase getMethodPhase() { return phase; }
    public Object getReturnValue() { return returnValue; }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
        this.isModified = true;
    }

    public boolean isModified() { return isModified; }
    public void setModified(boolean modified) { isModified = modified; }

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public enum MethodPhase {
        RETURN,
        AFTER
    }

    public enum MethodType {
        GET_HEALTH,
        IS_DEAD_OR_DYING,
        IS_ALIVE,
        IS_REMOVED
    }
}