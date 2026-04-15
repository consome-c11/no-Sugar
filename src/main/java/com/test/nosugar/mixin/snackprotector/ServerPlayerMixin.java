package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.utils.entity.LivingEntityUtils;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 0)
public class ServerPlayerMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void snackProtector$cancelDie(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ILivingEntity iliving && (iliving.isErased(self.getUUID()) || iliving.isErased())) return;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player) && self.getHealth() > 0.f) {
            ci.cancel();
            //System.out.println("isAlive: " + LivingEntityUtils.isAlive(self) + " isDeadOrDying: " + LivingEntityUtils.isDeadOrDying(self) + " Health: " + LivingEntityUtils.getHealth(self));
        }
    }

}
