package com.test.nosugar.utils;

import net.minecraft.world.level.block.state.BlockState;

public interface ILevelChunkSection {

    BlockState forceSetBlockState(int x, int y, int z, BlockState newState, boolean useChecked);
}

