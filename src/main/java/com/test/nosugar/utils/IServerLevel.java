package com.test.nosugar.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IServerLevel {

    boolean forceSetBlock(BlockPos pos, BlockState newState, int flags, boolean isMoving);
}
