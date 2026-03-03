package com.test.nosugar.mixin.sugar_totem;

import com.test.nosugar.additional.SugarTotem;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 0)
public abstract class EntityMixin implements ILivingEntity {

    @Unique
    private static long LastDeathTime = 0L;
    @Unique
    private static int DeathCount = 0;

    @Override
    public int getDeathCount() {
        return DeathCount;
    }

    @Override
    public void setDeathCount(int count) {
        DeathCount = count;
    }

    @Override
    public long getLastDeathTime() {
        return LastDeathTime;
    }

    @Override
    public void setLastDeathTime(long time) {
        LastDeathTime = time;
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, index = 2)
    private float modifyHurtAmount(float originalAmount) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SugarTotem.hasTotem(player) && self.getHealth() - originalAmount < 1) {
            //SugarTotem.recordDeath(player);
            return 0.0F;
        }
        return originalAmount;
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void sugartotem$cancelDie(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SugarTotem.hasTotem(player)) {

            ci.cancel();
            //self.setHealth(self.getMaxHealth());
        }
    }

    @Inject(method = "isAlive", at = @At("HEAD"), cancellable = true)
    private void sugartotem$isAlive(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SugarTotem.hasTotem(player)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "isDeadOrDying", at = @At("HEAD"), cancellable = true)
    private void sugartotem$isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SugarTotem.hasTotem(player)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "getHealth", at = @At("RETURN"), cancellable = true)
    private void sugartotem$getHealth(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SugarTotem.hasTotem(player) && cir.getReturnValue() < 1) {
            //SugarTotem.recordDeath(player);
            //System.out.println("Death count: " + DeathCount);
            cir.setReturnValue(1.f);
            cir.cancel();
        }
    }
}
