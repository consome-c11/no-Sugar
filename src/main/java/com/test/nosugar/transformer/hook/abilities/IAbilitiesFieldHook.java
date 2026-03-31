package com.test.nosugar.transformer.hook.abilities;

import com.test.nosugar.transformer.event.AbilitiesFieldEvent;

public interface IAbilitiesFieldHook {
    boolean onWriteMayFly(Object abilities, boolean original, String fieldName, AbilitiesFieldEvent.FieldPhase phase);
    boolean onWriteFlying(Object abilities, boolean original, String fieldName, AbilitiesFieldEvent.FieldPhase phase);
}