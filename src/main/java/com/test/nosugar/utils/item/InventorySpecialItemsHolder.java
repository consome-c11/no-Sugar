package com.test.nosugar.utils.item;

import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.Set;

public class InventorySpecialItemsHolder {
    private static Set<Item> SPECIAL_ITEMS = Collections.emptySet();

    public static void setSpecialItems(Set<Item> items) {
        if (SPECIAL_ITEMS.isEmpty()) {
            SPECIAL_ITEMS = Collections.unmodifiableSet(items);
        }
    }

    public static boolean isSpecialItem(Item item) {
        return SPECIAL_ITEMS.contains(item);
    }
}
