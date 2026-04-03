package com.test.nosugar.transformer.hook.abilities;

import com.test.nosugar.transformer.NoSugarBus;
import com.test.nosugar.transformer.event.AbilitiesFieldEvent;
import net.minecraft.world.entity.player.Abilities;

public class AbilitiesFieldImpl implements IAbilitiesFieldHook {

    public static final IAbilitiesFieldHook INSTANCE = new AbilitiesFieldImpl();

    private AbilitiesFieldImpl() {}

    @Override
    public boolean onWriteMayFly(Object abilities, boolean original, String fieldName, AbilitiesFieldEvent.FieldPhase phase) {
        return postAndGet(abilities, AbilitiesFieldEvent.FieldType.MAY_FLY, fieldName, original);
    }

    @Override
    public boolean onWriteFlying(Object abilities, boolean original, String fieldName, AbilitiesFieldEvent.FieldPhase phase) {
        return postAndGet(abilities, AbilitiesFieldEvent.FieldType.IS_FLYING, fieldName, original);
    }

    private boolean postAndGet(Object abilitiesObj, AbilitiesFieldEvent.FieldType type, String fieldName, boolean original) {
        AbilitiesFieldEvent event = new AbilitiesFieldEvent(abilitiesObj, type, fieldName, original);
        NoSugarBus.BUS.post(event);

        if (event.getNewValue() instanceof Boolean bool) {
            return bool;
        }
        return original;
    }
}