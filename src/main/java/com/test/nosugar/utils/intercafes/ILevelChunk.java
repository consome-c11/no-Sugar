package com.test.nosugar.utils.intercafes;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface ILevelChunk {
    BlockState forceSetBlockState(ServerLevel level, BlockPos pos, BlockState newState, boolean isMoving);
}
