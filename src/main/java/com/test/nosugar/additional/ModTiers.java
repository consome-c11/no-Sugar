package com.test.nosugar.additional;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public class ModTiers {
    public static final Tier WORLD_DESTROYER_TIER = new ForgeTier(
            1919, // harvestLevel
            0, // durability
            Float.POSITIVE_INFINITY, // miningSpeed
            Float.POSITIVE_INFINITY, // attackDamageBonus
            30, // enchantmentValue
            BlockTags.NEEDS_DIAMOND_TOOL,
            () -> Ingredient.EMPTY
    );
    public static final Tier ERASER_TIER = new ForgeTier(
            1919,
            0,
            Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY,
            30,
            BlockTags.NEEDS_DIAMOND_TOOL,
            () -> Ingredient.EMPTY
    );
    public static final Tier TAIL_OF_NINE_TIER = new ForgeTier(
            999,
            0,
            35.0F,
            8,
            30,
            BlockTags.NEEDS_DIAMOND_TOOL,
            () -> Ingredient.EMPTY
    );
}
