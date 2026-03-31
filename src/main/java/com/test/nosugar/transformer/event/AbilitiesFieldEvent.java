package com.test.nosugar.transformer.event;

import net.minecraft.world.entity.player.Abilities;
import net.minecraftforge.eventbus.api.Event;

public class AbilitiesFieldEvent extends Event {
    private final Abilities abilities;
    private final FieldType type;
    private final String fieldName;
    private final Object oldValue;
    private Object newValue;

    public enum FieldType {
        MAY_FLY,
        IS_FLYING
    }

    public enum FieldPhase {
        BEFORE,
        AFTER
    }

    public AbilitiesFieldEvent(Abilities abilities, FieldType type, String fieldName, Object value) {
        this.abilities = abilities;
        this.type = type;
        this.fieldName = fieldName;
        this.oldValue = value;
        this.newValue = value;
    }

    public Abilities getAbilities() { return abilities; }
    public FieldType getType() { return type; }
    public Object getNewValue() { return newValue; }
    public void setNewValue(Object newValue) { this.newValue = newValue; }
}