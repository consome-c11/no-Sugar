package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$ontick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            ci.cancel();
        }
    }
}
