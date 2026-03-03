package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.additional.ModDamageTypes;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = Entity.class)
public class EntityMixin {

    /*@Inject(method = "shouldBeSaved", at = @At("HEAD"), cancellable = true)//なんかあったら怖いし一応?
    private void eraseGuard(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ILivingEntity erase && erase.isErased()) {
            cir.setReturnValue(false);
        }
    }*/

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void nosugar$isInvulnerableto(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        boolean iserase = source.is(ModDamageTypes.ERASE);
        if (iserase) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nosugar$onTick(CallbackInfo ci) {
        if ((Object) this instanceof ILivingEntity living && living.isErased()) {
            //if (!Config.isNormalDieEntity(((Entity)((Object)this))))ci.cancel();
        }
    }

    @Inject(method = "isRemoved", at = @At("HEAD"), cancellable = true)
    private void nosugar$onisRemoved(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (!self.level().isClientSide && self instanceof ILivingEntity living && living.isErased()) {
            cir.setReturnValue(true);
        }
    }

}