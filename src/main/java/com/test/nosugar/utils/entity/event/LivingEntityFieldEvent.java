package com.test.nosugar.utils.entity.event;

import cpw.mods.modlauncher.ClassTransformer;
import cpw.mods.cl.ModuleClassLoader;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.locating.IModLocator;

public class LivingEntityFieldEvent extends Event {

    private final Object entity;
    private final FieldType fieldType;
    private final String fieldName;
    private final FieldPhase phase;
    private Object newValue;
    private boolean isModified;
    private boolean forwarded = false;

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public enum FieldPhase {
        BEFORE
    }

    public LivingEntityFieldEvent() {
        this.entity = null;
        this.fieldType = null;
        this.fieldName = null;
        this.phase = FieldPhase.BEFORE;
        this.newValue = null;
        this.isModified = false;
    }

    public LivingEntityFieldEvent(LivingEntityFieldEvent oldEvent) {
        this.entity = oldEvent.getEntity();
        this.fieldType = oldEvent.getFieldType();
        this.fieldName = oldEvent.getFieldName();
        this.phase = oldEvent.getFieldPhase();
        this.newValue = oldEvent.getNewValue();
        this.isModified = oldEvent.isModified();
    }

    public LivingEntityFieldEvent(Object entity, FieldType fieldType, String fieldName,
                                  FieldPhase phase, Object originalValue) {
        this.entity = entity;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.phase = phase;
        this.newValue = originalValue;
        this.isModified = false;
    }

    public LivingEntityFieldEvent(Object entity, FieldType fieldType, String fieldName, Object originalValue) {
        this(entity, fieldType, fieldName, FieldPhase.BEFORE, originalValue);
    }

    public Object getEntity() { return entity; }
    public FieldType getFieldType() { return fieldType; }
    public String getFieldName() { return fieldName; }
    public FieldPhase getFieldPhase() { return phase; }
    public Object getNewValue() { return newValue; }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
        this.isModified = true;
    }

    public boolean isModified() { return isModified; }
    public void setModified(boolean modified) { isModified = modified; }

    public enum FieldType {
        HURT_TIME,//f_20916_:I
    }
}