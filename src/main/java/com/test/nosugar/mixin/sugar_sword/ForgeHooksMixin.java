package com.test.nosugar.mixin.sugar_sword;

import com.test.nosugar.additional.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

    @Inject(
            method = "getLootingLevel(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)I",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void onGetLootingLevel(Entity target, Entity killer, DamageSource cause, CallbackInfoReturnable<Integer> cir) {
        if (killer instanceof Player player && !player.level().isClientSide) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            boolean hasEraser = !main.isEmpty() && main.getItem() == ModItems.SUGAR_SWORD.get()
                    || !off.isEmpty() && off.getItem() == ModItems.SUGAR_SWORD.get();

            boolean hasWorldDestroyer = !main.isEmpty() && main.getItem() == ModItems.WORLD_DESTROYER.get()
                    || !off.isEmpty() && off.getItem() == ModItems.WORLD_DESTROYER.get();
            if (hasEraser || hasWorldDestroyer) {
                cir.setReturnValue(7);
            }
        }
    }

}