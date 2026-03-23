package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.sql.Time;

@Mixin(Timer.class)
public abstract class TimerMixin {
    @Unique private int lastTick;

    @Inject(method = "advanceTime", at = @At("HEAD"), cancellable = true)
    private void nosugar$advanceTime(long currentMs, CallbackInfoReturnable<Integer> cir) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        var level = mc.player.level();
        if (TimeStopManager.isStopped(level) && !TimeStopManager.CanMove(mc.player)) {
            /*((TimerAccessor) this).setLastMs(currentMs);
            cir.setReturnValue(lastTick);*/
            //cir.setReturnValue(0);
            //cir.cancel();
        }
    }

    /*@Inject(method = "advanceTime", at = @At("RETURN"))
    private void nosugar$captureReturn(long currentMs, CallbackInfoReturnable<Integer> cir) {
        lastTick = cir.getReturnValue();
    }*/
}