package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void nosugar$onInit(CallbackInfo ci) {
        TimeStopManager.clearLevel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$onTick(BooleanSupplier p_8794_, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (!TimeStopManager.isStopped(level)) {
            return;
        }

        ci.cancel();
    }

}
