package com.test.nosugar.mixin.sugar_sword;

import net.minecraft.world.level.entity.ChunkEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChunkEntities.class)
public interface ChunkEntitiesAccessor {
    @Accessor("entities")
    List<Object> getEntities();
}
