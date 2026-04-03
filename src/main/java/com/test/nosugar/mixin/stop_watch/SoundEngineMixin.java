package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;


@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Unique
    private boolean prevstop;

    @Inject(method = "tick", at = @At("HEAD"))
    private void handleTimeResume(boolean paused, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (prevstop && !TimeStopManager.isStopped(mc.level)) {
            for (SoundInstance sound : nosugar$delayedSounds) {
                ((SoundEngineAccessor)this).playSound(sound);
            }
            nosugar$delayedSounds.clear();
        }

        prevstop = TimeStopManager.isStopped(mc.level);
    }

    @ModifyVariable(
            method = "tick",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private boolean nosugar$ontick(boolean originalPaused) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            return true;
        }
        return originalPaused;
    }

    @Unique
    private final List<SoundInstance> nosugar$delayedSounds = new ArrayList<>();

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void nosugar$onplay(SoundInstance sound, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            if (sound.canPlaySound()) {
                nosugar$delayedSounds.add(sound);
            }
            ci.cancel();
        }
    }
}
