package com.test.nosugar.mixin.halo_of_sugar;

import com.test.nosugar.utils.entity.FlyManager;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void nosugar$onAiStepHEAD(CallbackInfo ci) {
        FlyManager.setCanDisableFly(true);
    }
    @Inject(method = "aiStep", at = @At("RETURN"))
    private void nosugar$onAiStepRETURN(CallbackInfo ci) {
        FlyManager.setCanDisableFly(false);
    }
}
