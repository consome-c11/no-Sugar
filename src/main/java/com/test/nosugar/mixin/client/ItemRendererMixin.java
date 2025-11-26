package com.test.nosugar.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.nosugar.items.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique
    private static final List<RegistryObject<Item>> AFFECTED_ITEMS = List.of(
            ModItems.SNACK_HELMET,
            ModItems.SNACK_CHESTPLATE,
            ModItems.SNACK_LEGGINGS,
            ModItems.SNACK_BOOTS,
            ModItems.NULL_INGOT
    );

    @Unique
    private static List<String> AFFECTED_ITEM_IDS = List.of(
            "eraser:sugar_eraser"
    );
    @Unique
    private static DynamicTexture dynTex = null;
    @Unique
    private static ResourceLocation dynLoc = null;
    @Unique
    private static NativeImage img = null;

    @Unique
    private static List<Item> getAffectedItems() {
        return AFFECTED_ITEMS.stream()
                .filter(RegistryObject::isPresent)
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }

    @Unique
    private static boolean shouldAffect(ItemStack stack, ItemDisplayContext ctx) {
        boolean inHand =
                ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                        ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                        ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                        ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

        boolean inGui = ctx == ItemDisplayContext.GUI;
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        List<Item> currentAffectedItems = getAffectedItems();
        return (inHand || inGui) && (
                ModItems.getAllItems().stream().anyMatch(stack::is) ||
                        currentAffectedItems.stream().anyMatch(stack::is) || AFFECTED_ITEM_IDS.contains(itemId)
        );
    }

    @Unique
    private static boolean isHeldContext(ItemDisplayContext ctx) {
        return ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    /*@Inject(method = "render", at = @At("HEAD"))
    private void eraser$injectDynamic(ItemStack stack, ItemDisplayContext ctx, boolean leftHand,
                                      PoseStack poseStack, MultiBufferSource buffer,
                                      int packedLight, int packedOverlay, BakedModel model,
                                      CallbackInfo ci) {
        if (shouldAffect(stack, ctx)) {
            if(dynTex == null || dynLoc == null || img == null) {
                initTexture();
            }
            long time = System.currentTimeMillis();
            int color = waveGrayWhiteColor(time, 0, 700.0);
            System.out.println("alpha=" + ((color >>> 24) & 0xFF));

            img.fillRect(0, 0, img.getWidth(), img.getHeight(), color);
            dynTex.upload();

            RenderType dynType = RenderType.text(dynLoc);
            VertexConsumer vc = buffer.getBuffer(dynType);
            ((ItemRendererAccessor)(Object)this).callRenderModelLists(model, stack, packedLight, packedOverlay, poseStack, vc);
        }
    }*/

    @Unique
    public List<String> getAffectedItemIds() {
        return AFFECTED_ITEM_IDS;
    }

    @Unique
    public boolean add_toAffectedItemIds(String id) {
        return AFFECTED_ITEM_IDS.add(id);
    }

    /*@Inject( //W.I.P :)
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V",
            shift = At.Shift.AFTER
    )
    )
    private void onRenderModelLists(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (shouldAffect(stack, context)) {
            float[] color = ColorUtils.getGlintColor(stack);
            if (color != null && color.length >= 3 && RenderTypes.GLINT_COLORED_TYPE != null) {
                if (Shaders.glintColorUniform != null) {
                    Shaders.glintColorUniform.set(color[0], color[1], color[2]);
                }

                VertexConsumer glintConsumer = buffer.getBuffer(RenderTypes.GLINT_COLORED_TYPE);

                renderModelLists(model, stack, packedLight, packedOverlay, poseStack, glintConsumer);
            }
        }
    }*/

    @Unique
    private void renderModelLists(BakedModel model, ItemStack stack, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer vertexConsumer) {
        net.minecraft.util.RandomSource randomsource = net.minecraft.util.RandomSource.create();
        long i = 42L;

        for(net.minecraft.core.Direction direction : net.minecraft.core.Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(poseStack, vertexConsumer, model.getQuads(null, direction, randomsource), stack, packedLight, packedOverlay);
        }

        randomsource.setSeed(42L);
        renderQuadList(poseStack, vertexConsumer, model.getQuads(null, null, randomsource), stack, packedLight, packedOverlay);
    }

    @Unique
    private void renderQuadList(PoseStack poseStack, VertexConsumer vertexConsumer, java.util.List<net.minecraft.client.renderer.block.model.BakedQuad> quads, ItemStack stack, int packedLight, int packedOverlay) {
        boolean flag = !stack.isEmpty();
        com.mojang.blaze3d.vertex.PoseStack.Pose posestack$pose = poseStack.last();

        for(net.minecraft.client.renderer.block.model.BakedQuad bakedquad : quads) {
            int i = -1;
            if (flag && bakedquad.isTinted()) {
                i = net.minecraft.client.Minecraft.getInstance().getItemColors().getColor(stack, bakedquad.getTintIndex());
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            vertexConsumer.putBulkData(posestack$pose, bakedquad, f, f1, f2, 1.0F, packedLight, packedOverlay, true);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void eraser$rotateInGui(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                    PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                    int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (context == ItemDisplayContext.GUI && shouldAffect(stack, context)) {
            long time = System.currentTimeMillis();
            float angle = (float) (Math.sin(time / 500.0) * 0.4);

            poseStack.pushPose();
            poseStack.translate(8.0F, 8.0F, 100.0F);
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angle));
            poseStack.translate(-8.0F, -8.0F, -100.0F);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void eraser$popGui(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                               PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                               int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (context == ItemDisplayContext.GUI && shouldAffect(stack, context)) {
            poseStack.popPose();
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void eraser$rotateHeld(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                   PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                   int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (isHeldContext(context) && shouldAffect(stack, context)) {
            long time = System.currentTimeMillis();
            float angle = (float) (Math.sin(time / 500.0) * 10.0);

            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void eraser$popHeld(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (isHeldContext(context) && shouldAffect(stack, context)) {
            poseStack.popPose();
        }
    }

}
