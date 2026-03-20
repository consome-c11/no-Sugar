package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.utils.entity.LivingEntityUtils;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "markHurt", at = @At("HEAD"), cancellable = true)
    private void snackProtector$onMarkHurt(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            ci.cancel();
            //self.setHealth(self.getMaxHealth());
        }
    }

    @Inject(method = "isRemoved", at = @At("HEAD"), cancellable = true)
    private void snackProtector$isRemoved(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ILivingEntity iliving && (iliving.isErased(self.getUUID()) || iliving.isErased())
                || self.getRemovalReason() == Entity.RemovalReason.CHANGED_DIMENSION ||
                self.getRemovalReason() == Entity.RemovalReason.DISCARDED/*痛い目見た*/) return;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            //System.out.println("isAlive: " + LivingEntityUtils.isAlive(player) + " isDeadOrDying: " + LivingEntityUtils.isDeadOrDying(player) + " Health: " + LivingEntityUtils.getHealth(player));
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
