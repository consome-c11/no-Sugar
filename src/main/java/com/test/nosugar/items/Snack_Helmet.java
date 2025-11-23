package com.test.nosugar.items;

import com.test.nosugar.additional.SnackArmor;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

public class Snack_Helmet extends ArmorItem {
    public Snack_Helmet() {
        super(SnackArmor.SNACK_MATERIAL, Type.HELMET,
                new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
}
