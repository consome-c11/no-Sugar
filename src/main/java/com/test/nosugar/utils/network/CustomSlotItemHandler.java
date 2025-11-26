package com.test.nosugar.utils.network;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CustomSlotItemHandler extends SlotItemHandler {

    public CustomSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE; // GUI表示や操作時の上限を設定
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return Integer.MAX_VALUE;
    }

}