package com.test.nosugar.compat.tconstruct;

import com.test.nosugar.NoSugar;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class Mods {
    public static final StaticModifier<NoLevelsModifier> SUGAR;
    public static final StaticModifier<NoLevelsModifier> TAIL_OF_NINE;
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(NoSugar.MODID);

    static {
        SUGAR = MODIFIERS.register("sugar", SugarMod::new);
        TAIL_OF_NINE = MODIFIERS.register("tail_of_nine", Tail_of_NineMod::new);
    }

    public static void register(IEventBus bus) {
        MODIFIERS.register(bus);
    }
}
