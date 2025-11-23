package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.mixin.eraser.LivingEntityAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityDataMixin {
    @Shadow private Entity entity;

    @Unique private static final EntityDataAccessor<Float> HEALTH_ID = LivingEntityAccessor.getDataHealthId();

    @Inject(
            method = "set",
            at = @At("HEAD"),
            cancellable = true
    )
    private <T> void preProtectHealthUpdate(EntityDataAccessor<T> accessor, T value, CallbackInfo ci) {
        if (accessor != HEALTH_ID || entity.level().isClientSide) {
            return;
        }

        if (!(entity instanceof LivingEntity living) || !(living instanceof Player player)) {
            return;
        }

        if (!SnackArmor.SnackProtector.isFullSet(player)) {
            return;
        }

        if (value instanceof Float newHealth && newHealth <= living.getHealth()) {
            ci.cancel();
        }
    }
}