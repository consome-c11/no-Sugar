package com.test.nosugar.additional;

import com.test.nosugar.NoSugar;
import com.test.nosugar.spells.GazeDeathSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, NoSugar.MODID);

    public static final RegistryObject<AbstractSpell> GAZE_DEATH =
            SPELLS.register("gaze_death", GazeDeathSpell::new);

    public static void register(IEventBus bus) {
        SPELLS.register(bus);
    }

}