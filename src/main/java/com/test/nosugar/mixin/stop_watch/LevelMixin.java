package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void nosugar$onGuardEntityTick(Consumer<T> p_46654_, T p_46655_, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (TimeStopManager.isStopped(level) && !TimeStopManager.CanMove(p_46655_)) {
            ci.cancel();
        }
    }
}