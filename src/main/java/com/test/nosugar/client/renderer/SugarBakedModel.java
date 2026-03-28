package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.nosugar.shader.ModShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class SugarBakedModel extends BakedModelWrapper<BakedModel> {

    public SugarBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    /*public BakedModel getLayerModel(int targetTintIndex) {
        return new LayerFilteringModel(this.originalModel, targetTintIndex);
    }*/

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return List.of(this);
    }

    /*private static class LayerFilteringModel extends BakedModelWrapper<BakedModel> {
        private final int targetTintIndex;

        public LayerFilteringModel(BakedModel original, int targetTintIndex) {
            super(original);
            this.targetTintIndex = targetTintIndex;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            return super.getQuads(state, side, rand).stream()
                    .filter(quad -> quad.getTintIndex() == targetTintIndex)
                    .collect(Collectors.toList());
        }
    }*/
}