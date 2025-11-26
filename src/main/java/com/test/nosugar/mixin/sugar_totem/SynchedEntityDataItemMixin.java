package com.test.nosugar.mixin.sugar_totem;

import com.test.nosugar.additional.SugarTotem;
import com.test.nosugar.mixin.sugar_sword.LivingEntityAccessor;
import com.test.nosugar.mixin.sugar_sword.SynchedEntityDataAccessor;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SynchedEntityData.DataItem.class)
public abstract class SynchedEntityDataItemMixin<T> {

    @Shadow
    private T value;

    @Shadow
    public abstract EntityDataAccessor<T> getAccessor();

    @Unique
    private T oldValue;
    private static final EntityDataAccessor<Float> HEALTH_ID = LivingEntityAccessor.getDataHealthId();

    @Unique private static java.lang.reflect.Field this$0;

    @Unique
    private SynchedEntityData getOuterSynchedData() {
        try {
            if (this$0 == null) {
                this$0 = getClass().getDeclaredField("this$0");
                this$0.setAccessible(true);
            }
            return (SynchedEntityData) this$0.get(this);
        } catch (Exception e) {
            return null;
        }
    }

    @Inject(method = "isDirty", at = @At("HEAD"), cancellable = true)
    private void sugarTotem$isDirty(CallbackInfoReturnable<Boolean> cir) {
        if (getAccessor() != HEALTH_ID || oldValue == null) {
            return;
        }
        SynchedEntityData synchedData = getOuterSynchedData();
        if (synchedData == null) {
            return;
        }

        Entity entity = ((SynchedEntityDataAccessor) synchedData).getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (!(entity instanceof LivingEntity living) || !(living instanceof Player player)) {
            return;
        }

        if (SugarTotem.hasTotem(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "value", at = @At("RETURN"), cancellable = true)
    private void sugarTotem$protectValueForWrite(CallbackInfoReturnable<SynchedEntityData.DataValue<T>> cir) {
        if (getAccessor() != HEALTH_ID ) {
            return;
        }
        if(oldValue == null && cir.getReturnValue().value() != null) {
            oldValue = cir.getReturnValue().value();
        }
        SynchedEntityData synchedData = getOuterSynchedData();
        if (synchedData == null) {
            return;
        }
        Entity entity = ((SynchedEntityDataAccessor) synchedData).getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (!(entity instanceof LivingEntity living) || !(living instanceof Player player)) {
            return;
        }
        if (SugarTotem.hasTotem(player) && cir.getReturnValue().value() instanceof Float val && val < 2) {
            SynchedEntityData.DataValue<T> safeDataValue = SynchedEntityData.DataValue.create(getAccessor(), oldValue);

            cir.setReturnValue(safeDataValue);
            //cir.cancel();
        }
    }
}