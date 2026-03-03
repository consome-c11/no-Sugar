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

@Mixin(SynchedEntityData.DataItem.class)
public abstract class SynchedEntityDataItemMixin<T> implements ISynchedEntityDataItem {
    @Unique
    private static final EntityDataAccessor<Float> HEALTH_ID = LivingEntityAccessor.getDataHealthId();
    @Unique
    private static java.lang.reflect.Field this$0;
    @Shadow
    private T value;
    @Unique
    private T oldValue;

    @Shadow
    public abstract EntityDataAccessor<T> getAccessor();

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

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    private void nosugar$setValue(T newValue, CallbackInfo ci) {
        if (oldValue == null) oldValue = newValue;
        if (getAccessor() == HEALTH_ID) {

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

            if (!SnackArmor.SnackProtector.isFullSet(player)) {
                return;
            }

            if (oldValue instanceof Float oldHealth && newValue instanceof Float newHealth) {
                if (newHealth <= oldHealth) {
                    ci.cancel();
                    this.value = oldValue;
                } else this.oldValue = this.value;
            }
        }
    }

    @Inject(method = "setDirty", at = @At("HEAD"), cancellable = true)
    private void nosugar$setDirty(boolean dirty, CallbackInfo ci) {
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

        if (!SnackArmor.SnackProtector.isFullSet(player)) {
            return;
        }

        if (oldValue instanceof Float oldHealth && this.value instanceof Float newHealth) {
            if (newHealth <= oldHealth) {
                this.value = oldValue;
                ci.cancel();
            } else this.oldValue = this.value;
        }
    }

    @Inject(method = "isDirty", at = @At("RETURN"), cancellable = true)
    private void nosugar$isDirty(CallbackInfoReturnable<Boolean> cir) {
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
        if (!(entity instanceof LivingEntity living) || !(living instanceof Player player) || !SnackArmor.SnackProtector.isFullSet(player)) {
            return;
        }
        if (oldValue instanceof Float oldHealth && this.value instanceof Float newHealth) {
            if (newHealth <= oldHealth) {
                this.value = oldValue;
            }
        }
        cir.setReturnValue(false);
    }

    @Inject(method = "value", at = @At("RETURN"), cancellable = true)//idk
    private void nosugar$value(CallbackInfoReturnable<SynchedEntityData.DataValue<T>> cir) {
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

        if (!(entity instanceof LivingEntity living) || !(living instanceof Player player) || !SnackArmor.SnackProtector.isFullSet(player)) {
            return;
        }
        if (cir.getReturnValue().value() instanceof Float newHealth && this.value instanceof Float oldHealth && newHealth < oldHealth) {
            SynchedEntityData.DataValue<T> safeDataValue = SynchedEntityData.DataValue.create(getAccessor(), oldValue);
            this.value = oldValue;
            cir.setReturnValue(safeDataValue);
            cir.cancel();
        }
    }

    @Override
    public void CheckData() {
        if (oldValue instanceof Float oldHealth && this.value instanceof Float newHealth) {
            //System.out.println("Old Health: " + oldHealth + " New Health: " + newHealth);//あぁ～コンソールスパムの音ぉ～
            if (newHealth <= oldHealth) {
                this.value = oldValue;
            } else this.oldValue = this.value;
        }
    }

    @Override
    public T GetOldData() {
        return oldValue;
    }
}