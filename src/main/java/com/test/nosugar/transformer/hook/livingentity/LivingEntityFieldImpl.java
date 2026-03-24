package com.test.nosugar.transformer.hook.livingentity;

import com.test.nosugar.NoSugar;
import com.test.nosugar.transformer.event.LivingEntityFieldEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public class LivingEntityFieldImpl implements ILivingEntityFieldHook {

    public static final ILivingEntityFieldHook INSTANCE = new LivingEntityFieldImpl();

    private LivingEntityFieldImpl() {}

    @Override
    public int onWriteHurtTime(LivingEntity entity, int original, String fieldName, LivingEntityFieldEvent.FieldPhase phase) {
        LivingEntityFieldEvent event = new LivingEntityFieldEvent(
                entity,
                LivingEntityFieldEvent.FieldType.HURT_TIME,
                fieldName,
                original
        );
        MinecraftForge.EVENT_BUS.post(event);
        Object returnValue = event.getNewValue();

        if (returnValue instanceof Number num) {
            return num.intValue();
        }

        if (returnValue != null) {
            NoSugar.LOGGER.warn("Invalid return type from event: expected Number, got {}", returnValue.getClass());
        } else {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stack) {
                String className = element.getClassName();
                NoSugar.LOGGER.info("onWriteHurtTime NOT updated called from: {}.{}({}:{})",
                        className, element.getMethodName(), element.getFileName(), element.getLineNumber());
                break;
            }
        }
        return original;
    }
}