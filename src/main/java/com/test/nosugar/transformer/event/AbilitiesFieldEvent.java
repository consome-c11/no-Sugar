package com.test.nosugar.transformer.event;

import net.minecraft.world.entity.player.Abilities;
import net.minecraftforge.eventbus.api.Event;

public class AbilitiesFieldEvent extends Event {
    private final Object abilities;
    private final FieldType type;
    private final String fieldName;
    private final Object oldValue;
    private Object newValue;
    public boolean isModified;
    private boolean forwarded = false;

    public AbilitiesFieldEvent(AbilitiesFieldEvent oldEvent) {
        this.abilities = oldEvent.getAbilities();
        this.fieldName = oldEvent.getFieldName();
        this.newValue = oldEvent.getNewValue();
        this.type = oldEvent.getType();
        this.oldValue = oldEvent.getOldValue();
        this.isModified = false;
    }
    public AbilitiesFieldEvent() {
        this.abilities = null;
        this.type = null;
        this.fieldName = "";
        this.oldValue = null;
        this.newValue = null;
        this.isModified = false;
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public enum FieldType {
        MAY_FLY,
        IS_FLYING
    }

    public enum FieldPhase {
        BEFORE,
        AFTER
    }

    public AbilitiesFieldEvent(Object abilities, FieldType type, String fieldName, Object value) {
        this.abilities = abilities;
        this.type = type;
        this.fieldName = fieldName;
        this.oldValue = value;
        this.newValue = value;
        this.isModified = false;
    }

    public Object getAbilities() { return abilities; }
    public FieldType getType() { return type; }
    public Object getNewValue() { return newValue; }
    public void setNewValue(Object newValue) { this.newValue = newValue; }
    public String getFieldName() {
        return fieldName;
    }
    public void setModified(boolean b) {
        this.isModified = b;
    }
    public Object getOldValue() {
        return oldValue;
    }

}