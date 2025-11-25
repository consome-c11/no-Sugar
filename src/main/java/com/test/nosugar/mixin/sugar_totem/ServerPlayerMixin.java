package com.test.nosugar.mixin.sugar_totem;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.additional.SugarTotem;
import com.test.nosugar.mixin.eraser.LivingEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 0)
public class ServerPlayerMixin {
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void snackProtector$cancelDie(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && SugarTotem.hasTotem(player)) {
            ((LivingEntityAccessor)self).setDeadFlag(false);
            SugarTotem.onDead(player);
            ci.cancel();
        }
    }

}