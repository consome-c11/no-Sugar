package com.test.nosugar.client.renderer;

import com.test.nosugar.entity.Sand_Bag;
import com.test.nosugar.utils.Res;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SandBagRenderer extends MobRenderer<Sand_Bag, SandBagModel> {
    private static final ResourceLocation TEXTURE =
            Res.getResource("minecraft", "textures/entity/illager/vex.png");

    public SandBagRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SandBagModel(ctx.bakeLayer(ModelLayers.VEX)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(Sand_Bag entity) {
        return TEXTURE;
    }
}