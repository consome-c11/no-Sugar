package com.test.nosugar.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.item.InventorySpecialItemsHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @WrapOperation(method = "dropAll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean nosugar$dropAll(ItemStack instance, Operation<Boolean> original) {
        boolean empty = original.call(instance);

        if (!empty && InventorySpecialItemsHolder.isSpecialItem(instance.getItem())) {
            return true;
        }

        return empty;
    }
}