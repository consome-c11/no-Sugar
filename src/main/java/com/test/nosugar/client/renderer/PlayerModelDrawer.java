package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModelDrawer {

    public static void renderEntity(LivingEntity entity, PoseStack poseStack,
                                    float x, float y, float z,
                                    float scale, float partialTick) {

        if (entity == null || Minecraft.getInstance().level == null) return;

        poseStack.pushPose();

        // 位置とスケール
        poseStack.translate(x, y, z);
        poseStack.scale(scale, scale, scale);

        // 回転 (Y軸)
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180 - entity.getYRot()));

        // コンテキスト取得
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        EntityRenderer<? super LivingEntity> renderer = dispatcher.getRenderer(entity);

        if (renderer instanceof LivingEntityRenderer livingRenderer && livingRenderer instanceof PlayerRenderer playerRenderer) {
            int packedLight = 15728880;

            if (entity instanceof ClientEntityCache.ClientPreviewEntity preview) {
                preview.yBodyRot = entity.getYRot();
                preview.yHeadRot = entity.getYRot();

                if (ClientEntityCache.entityCache.get(entity.getId()).isAttacking) {
                    preview.swing(InteractionHand.MAIN_HAND);
                }
            }

            playerRenderer.render(
                    (AbstractClientPlayer)entity,
                    0.0f,
                    partialTick,
                    poseStack,
                    buffer,
                    packedLight
            );
        }

        buffer.endBatch();
        poseStack.popPose();
    }
}