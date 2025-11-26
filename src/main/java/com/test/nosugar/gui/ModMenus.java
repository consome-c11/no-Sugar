package com.test.nosugar.gui;

import com.test.nosugar.NoSugar;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, NoSugar.MODID);

    public static final RegistryObject<MenuType<BagMenu>> BAG_MENU =
            MENUS.register("bag_menu",
                    () -> IForgeMenuType.create((windowId, inv, buf) -> new BagMenu(windowId, inv, buf))
            );
}
