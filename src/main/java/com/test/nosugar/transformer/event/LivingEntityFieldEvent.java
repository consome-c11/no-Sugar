package com.test.nosugar.transformer.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public class LivingEntityFieldEvent extends Event {

    private final Entity entity;
    private final FieldType fieldType;
    private final String fieldName;
    private final FieldPhase phase;
    private Object newValue;
    private boolean isModified;

    public enum FieldPhase {
        BEFORE
    }

    public LivingEntityFieldEvent(Entity entity, FieldType fieldType, String fieldName,
                                  FieldPhase phase, Object originalValue) {
        this.entity = entity;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.phase = phase;
        this.newValue = originalValue;
        this.isModified = false;
    }

    public LivingEntityFieldEvent(Entity entity, FieldType fieldType, String fieldName, Object originalValue) {
        this(entity, fieldType, fieldName, FieldPhase.BEFORE, originalValue);
    }

    public Entity getEntity() { return entity; }
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