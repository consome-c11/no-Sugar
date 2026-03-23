package com.test.nosugar.mixin.sugar_sword;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityTickList.class)
public interface EntityTickListAccessor {
    @Accessor("active")
    Int2ObjectMap<Entity> getActive();

    @Accessor("active")
    void setActive(Int2ObjectMap<Entity> active);

    @Accessor("passive")
    Int2ObjectMap<Entity> getPassive();

    @Accessor("passive")
    void setPassive(Int2ObjectMap<Entity> passive);

    @Accessor("iterated")
    Int2ObjectMap<Entity> getIterated();
}
