package com.test.nosugar.mixin.stop_watch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Consumer;

@Mixin(EntityTickList.class)
public class EntityTickListMixin {

    @WrapOperation(method = "forEach", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private void nosugar$wrapEntityTick(Consumer<Entity> instance, Object entity, Operation<Void> original) {
        Entity targetEntity = (Entity) entity;

        if (TimeStopManager.isStopped(targetEntity.level()) && !TimeStopManager.CanMove(targetEntity)) {
            return;
        }

        original.call(instance, entity);
    }
}