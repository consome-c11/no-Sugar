package com.test.nosugar.compact.tconstruct;

import com.test.nosugar.NoSugar;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class Mods {
    public static final StaticModifier<NoLevelsModifier> SUGAR;
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(NoSugar.MODID);

    static {
        SUGAR = MODIFIERS.register("sugar", SugarMod::new);
    }

    public static void register(IEventBus bus) {
        MODIFIERS.register(bus);
    }
}
