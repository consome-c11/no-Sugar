package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$ontick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        EntityAccessor iself = (EntityAccessor) self;
        if(TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self)) {
            ci.cancel();
        }

    }
}
