package com.test.nosugar.mixin.stop_watch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void nosugar$onInit(CallbackInfo ci) {
        TimeStopManager.clearLevel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$onTick(BooleanSupplier p_104727_, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (TimeStopManager.isStopped(level)) {
            ci.cancel();
        }
    }

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nosugar$onAnimateTick(int p_104785_, int p_104786_, int p_104787_, CallbackInfo ci) {
        Level level = (Level) (Object) this;
        if (TimeStopManager.isStopped(level)) {
            ci.cancel();
        }
    }

}