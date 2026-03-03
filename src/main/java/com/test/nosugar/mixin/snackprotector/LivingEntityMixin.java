package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.mixin.sugar_sword.LivingEntityAccessor;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void snackProtector$hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            cir.cancel();
        }
    }

    @Inject(method = "handleDamageEvent", at = @At("HEAD"), cancellable = true)
    private void snackProtector$handleDamageEvent(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            ci.cancel();
        }
    }

    /*@Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void snackProtector$getHealth2(CallbackInfoReturnable<Float>  cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self instanceof ILivingEntity iliving && (iliving.isErased(self.getUUID()) || iliving.isErased())) {
            cir.setReturnValue(0.f);//意味わからんて
            return;
        }
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true) && !((ILivingEntity)self).isErased()) {
            cir.setReturnValue(player.getMaxHealth());
            //System.out.println("Health: " + cir.getReturnValue());
            cir.cancel();
        }
    }

    @Inject(method = "isAlive", at = @At("HEAD"), cancellable = true)
    private void snackProtector$isAlive(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self instanceof ILivingEntity iliving && (iliving.isErased(self.getUUID()) || iliving.isErased())) return;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "isDeadOrDying", at = @At("HEAD"), cancellable = true)
    private void snackProtector$isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self instanceof ILivingEntity iliving && (iliving.isErased(self.getUUID()) || iliving.isErased())) return;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }*/

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void snackProtector$cancelDie(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof ILivingEntity iliving && (iliving.isErased(self.getUUID()) || iliving.isErased())) return;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            ((LivingEntityAccessor) self).setDeadFlag(false);
            ci.cancel();
            //self.setHealth(self.getMaxHealth());
        }
    }


    @Inject(
            method = "knockback(DDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void snackProtector$onKnockback(double strength, double ratioX, double ratioZ, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof Player player) {

            if (SnackArmor.SnackProtector.isFullSet(player)) {
                entity.hurtTime = 0;
                ci.cancel();
            }
        }
    }

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MobEffectInstance snackProtector$modifyEffectInstance(MobEffectInstance original) {
        if ((Object) this instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
            return new MobEffectInstance(
                    original.getEffect(),
                    (original.getDuration() * 7),//効果7倍! アンパソマソ!
                    original.getAmplifier(),
                    original.isAmbient(),
                    original.isVisible(),
                    original.showIcon()
            );
        }
        return original;
    }

    @Inject(method = "baseTick", at = @At("HEAD"), cancellable = true)
    private void snackProtector$onbaseTick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            player.setAirSupply(player.getMaxAirSupply());
            player.hurtTime = 0;
            player.hurtMarked = false;
        }

    }

    @Inject(method = "animateHurt", at = @At("HEAD"), cancellable = true)
    private void snackProtector$onAnimateHurt(float flt, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player)) {
            player.hurtTime = 0;
            player.hurtMarked = false;
            ci.cancel();
        }
    }

}