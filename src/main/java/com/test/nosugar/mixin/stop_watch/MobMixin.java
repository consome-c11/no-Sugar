package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "serverAiStep", at= @At("HEAD"), cancellable = true)
    private void nosugar$onserverAiStep(CallbackInfo ci){
        Mob self = (Mob) (Object) this;
        if((TimeStopManager.isStopped(self.level()) && !TimeStopManager.CanMove(self))
                || (self instanceof ILivingEntity iLiving && iLiving.isErased())) {
            ci.cancel();
        }
    }
}
