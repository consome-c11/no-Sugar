package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.test.nosugar.additional.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class HaloRenderer implements ICurioRenderer {

    private static ItemStack halo_layer1;
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack matrixStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel) {
            matrixStack.pushPose();
            humanoidModel.head.translateAndRotate(matrixStack);

            matrixStack.translate(0, -0.3, .85);
            matrixStack.scale(2, 2, 2);
            if(halo_layer1 == null){
                halo_layer1 = new ItemStack(ModItems.HALO_OF_SUGAR_DUMMY1.get());
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    halo_layer1,
                    ItemDisplayContext.FIXED,
                    light,
                    OverlayTexture.NO_OVERLAY,
                    matrixStack,
                    renderTypeBuffer,
                    slotContext.entity().level(),
                    0
            );

            matrixStack.popPose();
        }
    }
}
