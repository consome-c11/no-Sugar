package com.test.nosugar.mixin.common;

import com.test.nosugar.utils.InventorySpecialItemsHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(
            method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V",
            at = @At("HEAD")
    )
    private void onRestoreFrom(ServerPlayer p_9046_, boolean p_9047_, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        Inventory originalInventory = p_9046_.getInventory();
        Inventory newInventory = self.getInventory();

        copySpecialItems(originalInventory, newInventory);
    }

    private void copySpecialItems(Inventory from, Inventory to) {
        for (int i = 0; i < from.items.size(); i++) {
            var stack = from.items.get(i);
            if (!stack.isEmpty() && InventorySpecialItemsHolder.isSpecialItem(stack.getItem())) {
                if (to.items.get(i).isEmpty() || !InventorySpecialItemsHolder.isSpecialItem(to.items.get(i).getItem())) {
                    to.items.set(i, stack.copy());
                }
            }
        }

        for (int i = 0; i < from.armor.size(); i++) {
            var stack = from.armor.get(i);
            if (!stack.isEmpty() && InventorySpecialItemsHolder.isSpecialItem(stack.getItem())) {
                if (to.armor.get(i).isEmpty() || !InventorySpecialItemsHolder.isSpecialItem(to.armor.get(i).getItem())) {
                    to.armor.set(i, stack.copy());
                }
            }
        }

        for (int i = 0; i < from.offhand.size(); i++) {
            var stack = from.offhand.get(i);
            if (!stack.isEmpty() && InventorySpecialItemsHolder.isSpecialItem(stack.getItem())) {
                if (to.offhand.get(i).isEmpty() || !InventorySpecialItemsHolder.isSpecialItem(to.offhand.get(i).getItem())) {
                    to.offhand.set(i, stack.copy());
                }
            }
        }
    }
}