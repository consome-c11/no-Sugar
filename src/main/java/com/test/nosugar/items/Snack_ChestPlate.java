package com.test.nosugar.items;

import com.test.nosugar.additional.SnackArmor;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

public class Snack_ChestPlate extends ArmorItem {
    public Snack_ChestPlate() {
        super(SnackArmor.SNACK_MATERIAL, Type.CHESTPLATE,
                new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
}
