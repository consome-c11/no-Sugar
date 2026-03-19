package com.test.nosugar.transformer.hook.livingentity;

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
        return (Float) event.getReturnValue();
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
        return (Boolean) event.getReturnValue();
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
        return (Boolean) event.getReturnValue();
    }
}