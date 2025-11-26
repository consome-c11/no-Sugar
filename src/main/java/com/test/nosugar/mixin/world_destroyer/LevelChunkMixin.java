package com.test.nosugar.mixin.world_destroyer;

import com.test.nosugar.utils.intercafes.ILevelChunk;
import com.test.nosugar.utils.intercafes.ILevelChunkSection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements ILevelChunk {

    @Override
    public BlockState forceSetBlockState(ServerLevel level, BlockPos pos, BlockState newState, boolean isMoving) {
        LevelChunk self = (LevelChunk) (Object) this;

        int y = pos.getY();
        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        int localY = y & 15;

        LevelChunkSection section = self.getSection(self.getSectionIndex(y));
        BlockState oldState = ((ILevelChunkSection) section).forceSetBlockState(x, localY, z, newState, true);

        if (oldState == newState) {
            return null;
        }

        boolean wasEmpty = section.hasOnlyAir();
        boolean isEmpty = section.hasOnlyAir();
        if (wasEmpty != isEmpty) {
            level.getChunkSource().getLightEngine().updateSectionStatus(pos, isEmpty);
        }
        if (LightEngine.hasDifferentLightProperties(self, pos, oldState, newState)) {
            level.getChunkSource().getLightEngine().checkBlock(pos);
        }

        if (!level.isClientSide) {
            oldState.onRemove(level, pos, newState, isMoving);
        }

        if (oldState.hasBlockEntity() && (!oldState.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            self.removeBlockEntity(pos);
        }
        if (newState.hasBlockEntity()) {
            BlockEntity be = self.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
            if (be == null) {
                be = ((EntityBlock) newState.getBlock()).newBlockEntity(pos, newState);
                if (be != null) {
                    self.addAndRegisterBlockEntity(be);
                }
            } else {
                be.setBlockState(newState);
                ((LevelChunkAccessor) self).invokeUpdateBlockEntityTicker(be);
            }
        }

        return oldState;
    }
}