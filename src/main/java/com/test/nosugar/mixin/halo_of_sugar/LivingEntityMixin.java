package com.test.nosugar.mixin.halo_of_sugar;

import com.test.nosugar.utils.entity.EntityUtils;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V", shift = At.Shift.AFTER), cancellable = true)
    private void nosugar$actuallyHurt(DamageSource src, float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if(self instanceof ILivingEntity iliving && src.getEntity() instanceof Player player
                && EntityUtils.hasHaloOfSugar(player)) {
            if(amount > 0.f && amount != Float.POSITIVE_INFINITY && !Float.isNaN(amount) )iliving.setDelta(iliving.getDelta() + amount / 10.f);
        }
    }
}
