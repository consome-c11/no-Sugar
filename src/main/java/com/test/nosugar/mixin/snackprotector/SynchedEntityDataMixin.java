package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.mixin.sugar_sword.LivingEntityAccessor;
import com.test.nosugar.mixin.sugar_sword.SynchedEntityDataAccessor;
import com.test.nosugar.utils.interfaces.ISynchedEntityDataItem;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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

    @Inject(
            method = "packDirty",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void skipHealthSyncIfProtected(CallbackInfoReturnable<List<SynchedEntityData.DataValue<?>>> cir) {
        SynchedEntityDataAccessor dataAccessor = (SynchedEntityDataAccessor) this;
        Entity entity = dataAccessor.getEntity();

        if (entity == null || entity.level().isClientSide || !(entity instanceof LivingEntity living) || !(living instanceof Player player) || !SnackArmor.SnackProtector.isFullSet(player)) {
            return;
        }
        ((ISynchedEntityDataItem) ((SynchedEntityDataAccessor) player.getEntityData()).invokeGetItem(LivingEntityAccessor.getDataHealthId())).CheckData();
    }
}