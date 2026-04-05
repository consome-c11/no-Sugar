package com.test.nosugar.utils.entity.hook.abilities;


import com.test.nosugar.utils.entity.event.AbilitiesFieldEvent;

public interface IAbilitiesFieldHook {
    boolean onWriteMayFly(Object abilities, boolean original, String fieldName, AbilitiesFieldEvent.FieldPhase phase);
    boolean onWriteFlying(Object abilities, boolean original, String fieldName, AbilitiesFieldEvent.FieldPhase phase);
}