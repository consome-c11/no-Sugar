package com.test.nosugar.transformer.hook.livingentity;

import com.test.nosugar.transformer.event.LivingEntityFieldEvent;
import net.minecraft.world.entity.LivingEntity;

public interface ILivingEntityFieldHook {
    int onWriteHurtTime(Object entity, int original, String fieldName, LivingEntityFieldEvent.FieldPhase phase);
}