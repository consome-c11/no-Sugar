package com.test.nosugar.utils.entity.hook.abilities;


import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.entity.event.AbilitiesFieldEvent;
import com.test.nosugar.utils.entity.event.NoSugarBus;

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
        //NoSugar.LOGGER.info("ori: " + original);
        AbilitiesFieldEvent event = new AbilitiesFieldEvent(abilitiesObj, type, fieldName, original);
        NoSugarBus.BUS.post(event);
        //NoSugar.LOGGER.info("new val: {}", event.getNewValue());
        if (event.getNewValue() instanceof Boolean bool) {
            return bool;
        }
        return original;
    }
}