package com.test.nosugar.mixin.world_destroyer;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelChunk.class)
public interface LevelChunkAccessor {
    @Invoker("updateBlockEntityTicker")
    void invokeUpdateBlockEntityTicker(BlockEntity blockEntity);
}
