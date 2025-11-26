package com.test.nosugar;

import com.mojang.logging.LogUtils;
import com.test.nosugar.compact.spells.ModSpells;
import com.test.nosugar.compact.tconstruct.TConstruct;
import com.test.nosugar.client.ModCreativeTabs;
import com.test.nosugar.entity.ModEntities;
import com.test.nosugar.gui.ModMenus;
import com.test.nosugar.items.ModItems;
import com.test.nosugar.network.ModPackets;
import com.test.nosugar.utils.intercafes.InventorySpecialItemsHolder;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.test.nosugar.utils.Deets.IRONS_SPELLBOOKS;
import static com.test.nosugar.utils.Deets.require;

@SuppressWarnings("removal")
@Mod(NoSugar.MODID)
public class NoSugar {
    public static final String MODID = "nosugar";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NoSugar() {
        MinecraftForge.EVENT_BUS.register(this);

        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.ITEMS.register(modEventBus);
        ModItems.ADDON_ITEMS.register(modEventBus);
        ModItems.DUMMY_ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);//深夜テンションの時にコード書いてはダメだな
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        require(IRONS_SPELLBOOKS).run(() -> {
            ModSpells.register(modEventBus);
        });
        new TConstruct(modEventBus,FMLJavaModLoadingContext.get());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        List<Item> modItems = ModItems.getAllItems();
        Set<Item> itemSet = new HashSet<>(modItems);
        InventorySpecialItemsHolder.setSpecialItems(itemSet);
        ModPackets.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }
}
