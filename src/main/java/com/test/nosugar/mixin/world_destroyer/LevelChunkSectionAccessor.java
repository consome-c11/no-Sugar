package com.test.nosugar.mixin.world_destroyer;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelChunkSection.class)
public interface LevelChunkSectionAccessor {
    @Accessor("states")
    PalettedContainer<BlockState> getStates();

    @Accessor("nonEmptyBlockCount")
    short getNonEmptyBlockCount();

    @Accessor("nonEmptyBlockCount")
    void setNonEmptyBlockCount(short value);

    @Accessor("tickingBlockCount")
    short getTickingBlockCount();

    @Accessor("tickingBlockCount")
    void setTickingBlockCount(short value);

    @Accessor("tickingFluidCount")
    short getTickingFluidCount();

    @Accessor("tickingFluidCount")
    void setTickingFluidCount(short value);
}