package com.test.nosugar.mixin.sugar_sword;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(EntityLookup.class)
public interface EntityLookupAccessor<T extends EntityAccess> {
    @Accessor("byId")
    Int2ObjectMap<T> getById();

    @Mutable
    @Final
    @Accessor("byId")
    void setById(Int2ObjectMap<T> map);

    @Accessor("byUuid")
    Map<UUID, T> getByUuid();

    @Mutable
    @Final
    @Accessor("byUuid")
    void setByUuid(Map<UUID, T> map);

}
