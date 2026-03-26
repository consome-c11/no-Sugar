package com.test.nosugar;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.logging.LogUtils;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.client.ModCreativeTabs;
import com.test.nosugar.compat.slashblade.SERegister;
import com.test.nosugar.compat.spells.ModSpells;
import com.test.nosugar.compat.tconstruct.TConstruct;
import com.test.nosugar.entity.ModEntities;
import com.test.nosugar.gui.ModMenus;
import com.test.nosugar.network.ModPackets;
import com.test.nosugar.utils.Mapping;
import com.test.nosugar.utils.item.InventorySpecialItemsHolder;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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

import static com.test.nosugar.utils.Deets.*;

@SuppressWarnings("removal")
@Mod(NoSugar.MODID)
public class NoSugar {
    public static final String MODID = "nosugar";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static IEventBus modEventBus;

    public NoSugar() {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);


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
        require(SLASHBLADE).run(() -> {
            ModItems.SLASH_BLADE_ITEMS.register(modEventBus);
            SERegister.register(modEventBus);
        });
        new TConstruct(modEventBus, FMLJavaModLoadingContext.get());
        NoSugar.LOGGER.debug("[NoSugar] loaded");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        List<Item> modItems = ModItems.getAllItems();
        Set<Item> itemSet = new HashSet<>(modItems);
        InventorySpecialItemsHolder.setSpecialItems(itemSet);
        ModPackets.register();
        MixinExtrasBootstrap.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

}
