package com.test.nosugar.utils.entity.hook.livingentity;

import com.test.nosugar.agent.transformer.TransformerCore;

import com.test.nosugar.utils.entity.event.LivingEntityMethodEvent;
import com.test.nosugar.utils.entity.event.NoSugarBus;

public class LivingEntityMethodImpl implements ILivingEntityMethodHook {

    public static final ILivingEntityMethodHook INSTANCE = new LivingEntityMethodImpl();

    private LivingEntityMethodImpl() {}

    @Override
    public float getHealth(float original, Object entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.GET_HEALTH,
                phase,
                original
        );
        NoSugarBus.BUS.post(event);
        Object returnValue = event.getReturnValue();

        if (returnValue instanceof Number num) {
            return num.floatValue();
        }

        if (returnValue != null) {
            TransformerCore.LOGGER.warn("Invalid return type from event: expected Number, got {}", returnValue.getClass());
        }
        return original;
    }

    @Override
    public boolean isDeadOrDying(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.IS_DEAD_OR_DYING,
                phase,
                original
        );
        NoSugarBus.BUS.post(event);
        Object returnValue = event.getReturnValue();
        if (returnValue instanceof Boolean) {
            return (Boolean) returnValue;
        }
        if (returnValue != null) {
            TransformerCore.LOGGER.warn("Invalid return type from event: expected Boolean, got {}", returnValue.getClass());
        }
        return original;
    }

    @Override
    public boolean isAlive(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.IS_ALIVE,
                phase,
                original
        );
        NoSugarBus.BUS.post(event);
        Object returnValue = event.getReturnValue();
        if (returnValue instanceof Boolean) {
            return (Boolean) returnValue;
        }
        if (returnValue != null) {
            TransformerCore.LOGGER.warn("Invalid return type from event: expected Boolean, got {}", returnValue.getClass());
        }
        return original;
    }

    @Override
    public boolean isRemoved(boolean original, Object entity, LivingEntityMethodEvent.MethodPhase phase) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(
                entity,
                LivingEntityMethodEvent.MethodType.IS_REMOVED,
                phase,
                original
        );
        NoSugarBus.BUS.post(event);
        Object returnValue = event.getReturnValue();
        if (returnValue instanceof Boolean) {
            return (Boolean) returnValue;
        }
        if (returnValue != null) {
            TransformerCore.LOGGER.warn("Invalid return type from event: expected Boolean, got {}", returnValue.getClass());
        }
        return original;
    }
}