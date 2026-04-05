package com.test.nosugar.utils.entity.hook.livingentity;


import com.test.nosugar.utils.entity.event.LivingEntityFieldEvent;

public interface ILivingEntityFieldHook {
    int onWriteHurtTime(Object entity, int original, String fieldName, LivingEntityFieldEvent.FieldPhase phase);
}