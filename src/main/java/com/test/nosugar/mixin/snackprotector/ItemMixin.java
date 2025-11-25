package com.test.nosugar.mixin.snackprotector;

import com.test.nosugar.additional.SnackArmor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "finishUsingItem", at = @At("TAIL"))
    private void onItemFinishUsing(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof Player player) {
            if (SnackArmor.SnackProtector.isFullSet(player) && stack.getItem() == Items.COOKIE) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 220, 1));

            }
        }
    }
}
