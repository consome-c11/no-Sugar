package com.test.nosugar.mixin.world_destroyer;

import com.test.nosugar.utils.interfaces.ILevelChunkSection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements ILevelChunkSection {

    @Override
    public BlockState forceSetBlockState(int x, int y, int z, BlockState newState, boolean useChecked) {
        LevelChunkSection self = (LevelChunkSection) (Object) this;
        LevelChunkSectionAccessor acc = (LevelChunkSectionAccessor) self;

        BlockState oldState = useChecked
                ? acc.getStates().getAndSet(x, y, z, newState)
                : acc.getStates().getAndSetUnchecked(x, y, z, newState);

        var oldFluid = oldState.getFluidState();
        var newFluid = newState.getFluidState();

        if (!oldState.isAir()) {
            acc.setNonEmptyBlockCount((short) (acc.getNonEmptyBlockCount() - 1));
            if (oldState.isRandomlyTicking()) {
                acc.setTickingBlockCount((short) (acc.getTickingBlockCount() - 1));
            }
        }
        if (!oldFluid.isEmpty()) {
            acc.setTickingFluidCount((short) (acc.getTickingFluidCount() - 1));
        }

        if (!newState.isAir()) {
            acc.setNonEmptyBlockCount((short) (acc.getNonEmptyBlockCount() + 1));
            if (newState.isRandomlyTicking()) {
                acc.setTickingBlockCount((short) (acc.getTickingBlockCount() + 1));
            }
        }
        if (!newFluid.isEmpty()) {
            acc.setTickingFluidCount((short) (acc.getTickingFluidCount() + 1));
        }

        return oldState;
    }
}