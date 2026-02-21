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
    public float getHealth(float original, LivingEntity entity) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(entity, LivingEntityMethodEvent.MethodType.GET_HEALTH, original);
        //System.out.println("called getHealth ret: " + original);
        MinecraftForge.EVENT_BUS.post(event);
        return (Float) event.getReturnValue();
    }

    @Override
    public boolean isDeadOrDying(boolean original, LivingEntity entity) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(entity, LivingEntityMethodEvent.MethodType.IS_DEAD_OR_DYING, original);
        //System.out.println("called isDeadOrDying ret: " + original);
        MinecraftForge.EVENT_BUS.post(event);
        return (Boolean) event.getReturnValue();
    }

    @Override
    public boolean isAlive(boolean original, Entity entity) {
        LivingEntityMethodEvent event = new LivingEntityMethodEvent(entity, LivingEntityMethodEvent.MethodType.IS_ALIVE, original);
        //System.out.println("called isAlive ret: " + original);
        MinecraftForge.EVENT_BUS.post(event);
        return (Boolean) event.getReturnValue();
    }
}