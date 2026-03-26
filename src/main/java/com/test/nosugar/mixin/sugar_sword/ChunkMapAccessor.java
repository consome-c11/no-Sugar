package com.test.nosugar.mixin.sugar_sword;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {
    @Accessor("entityMap")
    Int2ObjectMap<?> getEntityMap();

    /*@Invoker("removeEntity")
    void removeEntity(Entity self);*/
}