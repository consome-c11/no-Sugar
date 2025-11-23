package com.test.nosugar.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CustomItemStackHandler extends ItemStackHandler {

    public CustomItemStackHandler(int size) {
        super(size);
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Integer.MAX_VALUE;
    }

}
