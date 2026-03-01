package com.test.nosugar.mixin.common;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.item.InventorySpecialItemsHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;


@Mixin(Inventory.class)
public class InventoryMixin {

    @Shadow
    public Player player;

    @Shadow
    private NonNullList<ItemStack> items;

    @Shadow
    private NonNullList<ItemStack> armor;

    @Shadow
    private NonNullList<ItemStack> offhand;

    @Inject(method = "dropAll", at = @At("HEAD"), cancellable = true)
    private void onDropAll(CallbackInfo callbackInfo) {
        if (hasSpecialItemInInventory()) {
            Chenged_dropAll();
            callbackInfo.cancel();
        }
    }

    private boolean hasSpecialItemInInventory() {
        List<NonNullList<ItemStack>> compartments = getCompartments();
        if (compartments == null) return false;

        for (NonNullList<ItemStack> compartment : compartments) {
            if (compartment == null) continue;
            for (ItemStack itemstack : compartment) {
                if (itemstack != null && !itemstack.isEmpty() && InventorySpecialItemsHolder.isSpecialItem(itemstack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void Chenged_dropAll() {
        List<NonNullList<ItemStack>> compartments = getCompartments();
        if (compartments == null) return;

        for (NonNullList<ItemStack> compartment : compartments) {
            if (compartment == null) continue;
            for (int i = 0; i < compartment.size(); ++i) {
                ItemStack itemstack = compartment.get(i);
                if (itemstack != null && !itemstack.isEmpty() && !InventorySpecialItemsHolder.isSpecialItem(itemstack.getItem())) {
                    if (this.player != null) {
                        this.player.drop(itemstack, true, false);
                        compartment.set(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    private List<NonNullList<ItemStack>> getCompartments() {
        if (this.items == null || this.armor == null || this.offhand == null) {
            NoSugar.LOGGER.debug("Warning: Inventory compartments not initialized yet.");
            return null;
        }
        return Arrays.asList(this.items, this.armor, this.offhand);
    }

    /*@Inject(
            method = "setItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSetItem(int slot, ItemStack stack, CallbackInfo ci) {

        ItemStack existingItemInSlot = this.items.get(slot);
        if (!PlayerDropStateHolder.getCanSetItem() &&
                (!existingItemInSlot.isEmpty() && InventorySpecialItemsHolder.isSpecialItem(existingItemInSlot.getItem()))) {
            ci.cancel();
        }
    }*/
}