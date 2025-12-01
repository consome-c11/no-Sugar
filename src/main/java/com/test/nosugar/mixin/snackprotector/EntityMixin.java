package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "markHurt", at = @At("HEAD"), cancellable = true)
    private void snackProtector$onMarkHurt(CallbackInfo ci) {
        Entity self = (Entity)(Object) this;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            ci.cancel();
            //self.setHealth(self.getMaxHealth());
        }
    }

}
