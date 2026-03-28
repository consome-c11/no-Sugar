package com.test.nosugar.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.client.renderer.SugarBakedModel;
import com.test.nosugar.client.renderer.SugarBowBakedModel;
import com.test.nosugar.shader.ModShaders;
import com.test.nosugar.utils.item.TicUtils;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    //キャッシュ使うとやっぱメモリ使用量とか下がるんかな
    @Unique
    private static final Set<Item> CACHED_AFFECTED_ITEMS = new HashSet<>();
    @Unique
    private static final Map<BakedModel, SugarBakedModel> CACHED_SUGAR_MODEL = new WeakHashMap<>();

    @Unique
    private static boolean cacheInitialized = false;

    @Unique
    private static final List<String> AFFECTED_ITEM_IDS = new CopyOnWriteArrayList<>(List.of(
            "eraser:sugar_eraser"
    ));
    @Unique
    private static final List<String> AFFECTED_ITEM_IDS2 = new CopyOnWriteArrayList<>(List.of(
            "nosugar:sugar_sword",
            "nosugar:world_destroyer",
            "nosugar:null_ingot",
            "nosugar:sugar_bow",
            "nosugar:halo_of_sugar",
            "nosugar:halo_of_sugar_layer1"
    ));

    @Unique
    private static int lastTrackedSlot = -1;
    @Unique
    private static long slotChangeTime = 0L;
    @Unique
    private static final long TRANSITION_DURATION_NS = 500_000_000L;

    @Unique
    private static void initCache() {
        if (cacheInitialized) return;
        if (ModItems.SNACK_HELMET.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_HELMET.get());
        if (ModItems.SNACK_CHESTPLATE.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_CHESTPLATE.get());
        if (ModItems.SNACK_LEGGINGS.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_LEGGINGS.get());
        if (ModItems.SNACK_BOOTS.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_BOOTS.get());
        if (ModItems.NULL_INGOT.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.NULL_INGOT.get());
        if (ModItems.SUGAR_SWORD.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SUGAR_SWORD.get());
        cacheInitialized = true;
    }

    @Unique
    private static boolean shouldAffect(ItemStack stack, ItemDisplayContext ctx) {
        initCache();
        boolean isHand = ctx.firstPerson() || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        boolean isGui = ctx == ItemDisplayContext.GUI;
        if (!(isHand || isGui)) return false;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return TicUtils.hasSugarMod(stack) ||
                ModItems.getAllItems().contains(stack.getItem()) ||
                CACHED_AFFECTED_ITEMS.contains(stack.getItem()) ||
                AFFECTED_ITEM_IDS.contains(itemId);
    }

    @Unique
    private static boolean shouldApplyShader(ItemStack stack) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return AFFECTED_ITEM_IDS2.contains(itemId);
    }

    @Unique
    private static boolean shouldApplyShader(ItemStack stack, int index) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return AFFECTED_ITEM_IDS2.contains(itemId);
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    private void nosugar$wrapShaderRender(ItemRenderer instance, BakedModel model, ItemStack stack, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer originalConsumer, Operation<Void> original, @Local(argsOnly = true) ItemDisplayContext context, @Local(argsOnly = true) MultiBufferSource bufferSource) {
        if (shouldApplyShader(stack)) {
            float time = (System.currentTimeMillis() % 100000) / 1000f;
            ModShaders.updateUniforms(time, 0, 0);
            VertexConsumer sugarConsumer = bufferSource.getBuffer(ModShaders.ITEM_RENDER_TYPE);
            original.call(instance, model, stack, packedLight, packedOverlay, poseStack, sugarConsumer);
        } else {
            original.call(instance, model, stack, packedLight, packedOverlay, poseStack, originalConsumer);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void nosugar$renderHead(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (!shouldAffect(stack, context)) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (context.firstPerson() && player != null && player.isUsingItem() && player.getUseItem() == stack) {
            return;
        }

        poseStack.pushPose();

        if (context == ItemDisplayContext.GUI) {
            updateSlotTracking(player);
            long timeNs = System.nanoTime();
            float transition = getTransitionProgress(timeNs);
            float baseAngle = (float) (Math.sin(timeNs / 500000000.0) * 1.7);

            if (isCurrentlySelectedSlotItem(stack, player) && transition > 0.01f) {
                float angle = (baseAngle * -180f) - 90F;
                float lerpAngle = angle * transition;
                float lerpScale = 1.0f + (baseAngle * 0.7F - 1.0f) * transition;
                poseStack.mulPose(Axis.ZP.rotationDegrees(lerpAngle));
                poseStack.scale(lerpScale, lerpScale, 1.0F);
            } else {
                poseStack.translate(1.0, 1.0, -20.0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(baseAngle));
                poseStack.translate(-1.0, -1.0, 20.0);
            }
        } else {
            long timeMs = System.currentTimeMillis();
            float angle = (float) (Math.sin(timeMs / 500.0) * 10.0);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void nosugar$renderReturn(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, BakedModel model, CallbackInfo ci) {
        if (!shouldAffect(stack, context)) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (context.firstPerson() && player != null && player.isUsingItem() && player.getUseItem() == stack) {
            return;
        }

        poseStack.popPose();
    }

    /*@WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    private void nosugar$wrapRenderModelLists(ItemRenderer instance, BakedModel model, ItemStack stack, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer originalConsumer, Operation<Void> original, @Local(argsOnly = true) MultiBufferSource bufferSource) {

        if (shouldApplyShader(stack)) {
            SugarBakedModel sugarModel = CACHED_SUGAR_MODEL.computeIfAbsent(model, SugarBakedModel::new);

            NoSugar.LOGGER.info("[NoSugar] Rendering with SugarModel for: " + stack.getItem());

            BakedModel layer0 = sugarModel.getLayerModel(0);
            original.call(instance, layer0, stack, packedLight, packedOverlay, poseStack, originalConsumer);

            float time = (System.currentTimeMillis() % 100000) / 1000f;
            ModShaders.updateUniforms(time, 0, 0);
            VertexConsumer sugarConsumer = bufferSource.getBuffer(ModShaders.ITEM_RENDER_TYPE);

            BakedModel layer1 = sugarModel.getLayerModel(1);
            original.call(instance, layer1, stack, packedLight, packedOverlay, poseStack, sugarConsumer);

        } else {
            original.call(instance, model, stack, packedLight, packedOverlay, poseStack, originalConsumer);
        }
    }*/

    @Unique
    private static float getTransitionProgress(long currentTime) {
        long elapsed = currentTime - slotChangeTime;
        if (elapsed >= TRANSITION_DURATION_NS) return 0f;
        float progress = (float) elapsed / TRANSITION_DURATION_NS;
        return 1f - (progress * progress);
    }

    @Unique
    private static void updateSlotTracking(@Nullable Player player) {
        if (player == null) return;
        int currentSlot = player.getInventory().selected;
        if (currentSlot != lastTrackedSlot) {
            lastTrackedSlot = currentSlot;
            slotChangeTime = System.nanoTime();
        }
    }

    @Unique
    private static boolean isCurrentlySelectedSlotItem(ItemStack stack, @Nullable Player player) {
        if (player == null) return false;
        ItemStack selected = player.getInventory().getSelected();
        return ItemStack.isSameItemSameTags(stack, selected) && stack.getCount() == selected.getCount();
    }
}