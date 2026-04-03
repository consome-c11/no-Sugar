package com.test.nosugar.transformer.hook.livingentity;

import com.test.nosugar.transformer.NoSugarBus;
import com.test.nosugar.transformer.TransformerCore;
import com.test.nosugar.transformer.event.LivingEntityFieldEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

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