package com.test.nosugar.utils.entity.hook.livingentity;

import com.test.nosugar.agent.transformer.TransformerCore;
import com.test.nosugar.utils.entity.event.LivingEntityFieldEvent;
import com.test.nosugar.utils.entity.event.NoSugarBus;

public class LivingEntityFieldImpl implements ILivingEntityFieldHook {

    public static final ILivingEntityFieldHook INSTANCE = new LivingEntityFieldImpl();

    private LivingEntityFieldImpl() {}

    @Override
    public int onWriteHurtTime(Object entity, int original, String fieldName, LivingEntityFieldEvent.FieldPhase phase) {
        LivingEntityFieldEvent event = new LivingEntityFieldEvent(
                entity,
                LivingEntityFieldEvent.FieldType.HURT_TIME,
                fieldName,
                original
        );
        NoSugarBus.BUS.post(event);
        Object returnValue = event.getNewValue();

        if (returnValue instanceof Number num) {
            return num.intValue();
        }

        if (returnValue != null) {
            TransformerCore.LOGGER.warn("Invalid return type from event (HURT_TIME): expected Number, got {}", returnValue.getClass());
        }
        return original;
    }
}