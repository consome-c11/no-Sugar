package com.test.nosugar.compat.slashblade;

import com.test.nosugar.NoSugar;
import com.test.nosugar.compat.slashblade.se.SugarSpecialEffect;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SERegister {
    public static final DeferredRegister<SpecialEffect> SPECIAL_EFFECTS =
            DeferredRegister.create(SpecialEffect.REGISTRY_KEY, NoSugar.MODID);

    public static final RegistryObject<SpecialEffect> SUGAR_SE =
            SPECIAL_EFFECTS.register("sugar_se", () -> new SugarSpecialEffect());

    public static void register(IEventBus modEventBus) {
        SPECIAL_EFFECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(SugarSpecialEffect.class);
    }
}