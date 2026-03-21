package com.test.nosugar.transformer.hook.livingentity;

import com.test.nosugar.NoSugar;
import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public class LivingEntityMethodsImpl implements ILivingEntityHook {

    public static final ILivingEntityHook INSTANCE = new LivingEntityMethodsImpl();

    private LivingEntityMethodsImpl() {}

    @Override
    public float getHealth(float original, LivingEntity entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.GET_HEALTH,
                phase,
                original
        );
        MinecraftForge.EVENT_BUS.post(event);
        Object returnValue = event.getReturnValue();

        if (returnValue instanceof Number num) {
            return num.floatValue();
        }

        if (returnValue != null) {
            NoSugar.LOGGER.warn("Invalid return type from event: expected Number, got {}", returnValue.getClass());
        } else {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stack) {
                String className = element.getClassName();
                NoSugar.LOGGER.info("getHealth NOT updated called from: {}.{}({}:{})",
                        className, element.getMethodName(), element.getFileName(), element.getLineNumber());
                break;

            }
        }
        return original;
    }

    @Override
    public boolean isDeadOrDying(boolean original, LivingEntity entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.IS_DEAD_OR_DYING,
                phase,
                original
        );
        MinecraftForge.EVENT_BUS.post(event);
        Object returnValue = event.getReturnValue();
        if (returnValue instanceof Boolean) {
            return (Boolean) returnValue;
        }
        if (returnValue != null) {
            NoSugar.LOGGER.warn("Invalid return type from event: expected Boolean, got {}", returnValue.getClass());
        }
        return original;
    }

    @Override
    public boolean isAlive(boolean original, Entity entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.IS_ALIVE,
                phase,
                original
        );
        MinecraftForge.EVENT_BUS.post(event);
        Object returnValue = event.getReturnValue();
        if (returnValue instanceof Boolean) {
            return (Boolean) returnValue;
        }
        if (returnValue != null) {
            NoSugar.LOGGER.warn("Invalid return type from event: expected Boolean, got {}", returnValue.getClass());
        }
        return original;
    }
}