package com.test.nosugar.items;

import com.test.nosugar.NoSugar;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.stream.Collectors;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NoSugar.MODID);

    public static final DeferredRegister<Item> DUMMY_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NoSugar.MODID);

    public static final DeferredRegister<Item> ADDON_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NoSugar.MODID);

    public static final RegistryObject<Item> SUGAR_SWORD =
            ITEMS.register("sugar_sword", () -> new SugarSword_Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WORLD_DESTROYER =
            ITEMS.register("world_destroyer", () -> new World_Destroyer_Item(new Item.Properties()));

    public static final RegistryObject<Item> NULL_INGOT =
            ITEMS.register("null_ingot", () -> new Null_Ingot_Item(new Item.Properties()));

    public static final RegistryObject<Item> SNACK_HELMET =
            ITEMS.register("snack_protect_helmet", () -> new Snack_Helmet());

    public static final RegistryObject<Item> SNACK_CHESTPLATE =
            ITEMS.register("snack_protect_chestplate", () -> new Snack_ChestPlate());

    public static final RegistryObject<Item> SNACK_LEGGINGS =
            ITEMS.register("snack_protect_leggings", () -> new Snack_Leggings());

    public static final RegistryObject<Item> SNACK_BOOTS =
            ITEMS.register("snack_protect_boots", () -> new Snack_Boots());

    public static final RegistryObject<Item> CANTEEN_ITEM =
            ITEMS.register("ultimate_canteen", () -> new UltimaCanteen(new Item.Properties().stacksTo(1)));

    /*public static final RegistryObject<Item> ERASER_ERASER =// thanks @mochi_753 :)
            ADDON_ITEMS.register("eraser_eraser", () -> new Eraser_Eraser(new Item.Properties().stacksTo(1)));*/

    public static final RegistryObject<Item> BLOCK_SUGER_ITEM =
            ITEMS.register("block_sugar", () -> new Block_Suger_Item(new Item.Properties().stacksTo(127)));

    public static final RegistryObject<Item> SUGAR_ARROW =
            ITEMS.register("sugar_arrow", () -> new Sugar_Arrow(new Item.Properties().stacksTo(127)));
    public static final RegistryObject<Item> SUGAR_BOW =
            ITEMS.register("sugar_bow", () -> new Sugar_Bow_Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SUGAR_TOTEM =
            ITEMS.register("sugar_totem", () -> new Sugar_Totem_Item());


    //f*cking bakedmodel load :(
    public static final RegistryObject<Item> SUGAR_BOW_DUMMY1 =
            DUMMY_ITEMS.register("sugar_bow_pulling_0", () -> new Sugar_Bow_Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SUGAR_BOW_DUMMY2 =
            DUMMY_ITEMS.register("sugar_bow_pulling_1", () -> new Sugar_Bow_Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SUGAR_BOW_DUMMY3 =
            DUMMY_ITEMS.register("sugar_bow_pulling_2", () -> new Sugar_Bow_Item(new Item.Properties().stacksTo(1)));

    public static List<Item> getAllItems() {
        return ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
}