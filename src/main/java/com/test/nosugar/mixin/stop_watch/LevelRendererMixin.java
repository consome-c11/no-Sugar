package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Unique
    private float lastRealPartialTicks;
    @Unique
    private boolean prevstop;

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateAllowedEntityTicks(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (TimeStopManager.CanMove(entity)) {
                    entity.tickCount++;
                }
            }
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void nosugar$controlSoundOnTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (TimeStopManager.isStopped(mc.level)) {
            mc.getSoundManager().pause();
        } else if(prevstop) {
            mc.getSoundManager().resume();
        }
        prevstop = TimeStopManager.isStopped(mc.level);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$onTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            ci.cancel();
        }
    }

    @ModifyVariable(
            method = "renderLevel",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float nosugar$modifyGlobalPartialTicks(float value) {
        this.lastRealPartialTicks = value;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            return 1.0E-6F;
        }
        return value;
    }

    @ModifyVariable(
            method = "renderEntity",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float nosugar$restoreTicksForSpecificEntity(float original, Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            if (TimeStopManager.CanMove(entity)) {
                return this.lastRealPartialTicks;
            }
            return 1.0E-6F;
        }
        return original;
    }
}