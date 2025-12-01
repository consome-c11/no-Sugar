package com.test.nosugar.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public enum ShootMode {
    SINGLE("Single Shot", 1),
    MULTI("Multi Shot (16)", 16),
    EXPLOSIVES("Explosives", 8);

    private static final String KEY = "SugarBowShootMode";
    private final String displayName;
    private final int shotCount;

    ShootMode(String displayName, int shotCount) {
        this.displayName = displayName;
        this.shotCount = shotCount;
    }

    public static ShootMode cycleMode(ItemStack stack) {
        ShootMode current = getMode(stack);
        ShootMode next = values()[(current.ordinal() + 1) % values().length];
        setMode(stack, next);
        return next;
    }

    public static ShootMode getMode(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(KEY)) {
            int ordinal = tag.getInt(KEY);
            if (ordinal >= 0 && ordinal < values().length) {
                return values()[ordinal];
            }
        }
        return SINGLE;
    }

    public static void setMode(ItemStack stack, ShootMode mode) {
        stack.getOrCreateTag().putInt(KEY, mode.ordinal());
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getShotCount() {
        return this.shotCount;
    }
}
