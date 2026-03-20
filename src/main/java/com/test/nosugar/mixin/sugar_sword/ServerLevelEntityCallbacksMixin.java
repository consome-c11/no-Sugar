package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.level.ServerLevel$EntityCallbacks", priority = 0)
public class ServerLevelEntityCallbacksMixin {
    @Inject(method = "onTrackingStart", at = @At("HEAD"), cancellable = true)
    private void nosugar$onTrackingStart(Entity entity, CallbackInfo ci) {
        if (entity instanceof ILivingEntity erase && erase.isErased() && !(entity instanceof Player)) {
            ci.cancel();
        }
    }
}