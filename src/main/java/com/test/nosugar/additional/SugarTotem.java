package com.test.nosugar.additional;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SugarTotem {

    public static boolean hasTotem(Player player) {
        if (player == null || player.level() == null || player.getInventory() == null) return false;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return (!mainHand.isEmpty() && mainHand.is(ModItems.SUGAR_TOTEM.get())) || (!offHand.isEmpty() && offHand.is(ModItems.SUGAR_TOTEM.get()));
    }

    public static void onDead(Player player) {
        Level level = player.level();
        if (level == null) return;
        consumeItemFromPlayerInventory(player, new ItemStack(Items.COOKIE), 1);
    }

    public static boolean consumeItemFromPlayerInventory(Player player, ItemStack targetItem, int count) {
        int slot = player.getInventory().findSlotMatchingItem(targetItem);
        if (slot != -1) {
            ItemStack stackInSlot = player.getInventory().getItem(slot);
            if (!stackInSlot.isEmpty() && stackInSlot.is(targetItem.getItem())) {
                if (stackInSlot.getCount() > 1) {
                    stackInSlot.shrink(1);
                } else {
                    player.getInventory().setItem(slot, ItemStack.EMPTY);
                }
                player.getInventory().setChanged();
                return true;
            }
        }
        return false;
    }
}
