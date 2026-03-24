package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Unique
    private float lastRealPartialTicks;

    @ModifyVariable(
            method = "renderLevel",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float captureRealTicks(float value) {
        this.lastRealPartialTicks = value;
        return value;
    }

    /*@ModifyVariable(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)V"),
            ordinal = 0,
            argsOnly = true
    )
    private float freezeWorldAfterCameraSetup(float original) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            return 0.0F;
        }

        return original;
    }*/

    @ModifyVariable(
            method = "renderEntity",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float restoreTicksForSpecificEntity(float original, Entity entity) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            if (TimeStopManager.CanMove(entity)) {
                return this.lastRealPartialTicks;
            }
            else return 0.f;
        }

        return original;
    }

}
