package com.test.nosugar.additional.tconstruct;

import com.test.nosugar.utils.Deets;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.test.nosugar.utils.Deets.TINKERSCONSTRUCT;

public class TConstruct {
    public TConstruct(IEventBus bus, FMLJavaModLoadingContext context) {
        Deets.require(TINKERSCONSTRUCT).run(() -> {
            Mods.register(bus);
        });
    }
}