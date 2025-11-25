package com.test.nosugar.additional;

import com.test.nosugar.utils.ILivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SugarTotem {

    public static boolean hasTotem(Player player) {
        Level level = player.level();
        if(level == null) return false;
        if(player.getMainHandItem().getItem() == ModItems.SUGAR_TOTEM.get() || player.getOffhandItem().getItem() == ModItems.SUGAR_TOTEM.get()) {
            return true;
        }
        return false;
    }

    public static void onDead(Player player) {
        Level level = player.level();
        if(level == null) return;
        consumeItemFromPlayerInventory(player,new ItemStack(Items.COOKIE),1);
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
