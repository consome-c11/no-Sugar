package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.nosugar.entity.Sand_Bag;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelPart;

public class SandBagModel extends EntityModel<Sand_Bag> {
    private final VexModel inner;

    public SandBagModel(ModelPart root) {
        this.inner = new VexModel(root);
    }

    @Override
    public void setupAnim(Sand_Bag entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        inner.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
