package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.nosugar.NoSugar;
import com.test.nosugar.shader.ModShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SugarBakedModel extends BakedModelWrapper<BakedModel> {

    public SugarBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        return List.of(this);
    }

}