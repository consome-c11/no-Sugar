package com.test.nosugar.items;

import com.test.nosugar.additional.SnackArmor;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class Snack_Boots extends ArmorItem {
    public Snack_Boots() {
        super(SnackArmor.SNACK_MATERIAL, Type.BOOTS,
                new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }
}
