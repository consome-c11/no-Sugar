package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    /*@Inject(
            method = "getRenderOffset(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void stopInterpolation(T entity, float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        if (TimeStopManager.isStopped(entity.level()) && !TimeStopManager.CanMove(entity)) {
            cir.setReturnValue(Vec3.ZERO);
        }
    }*/
}