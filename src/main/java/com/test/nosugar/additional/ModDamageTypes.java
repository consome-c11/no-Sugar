package com.test.nosugar.additional;

import com.test.nosugar.NoSuger;
import com.test.nosugar.utils.Res;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> ERASE =
            ResourceKey.create(Registries.DAMAGE_TYPE, Res.getResource(NoSuger.MODID, "erase"));
}
