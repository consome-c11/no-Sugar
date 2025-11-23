package com.test.nosugar.items;

import com.test.nosugar.additional.SnackArmor;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

public class Snack_Leggings extends ArmorItem {
    public Snack_Leggings() {
        super(SnackArmor.SNACK_MATERIAL, Type.LEGGINGS,
                new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
}
