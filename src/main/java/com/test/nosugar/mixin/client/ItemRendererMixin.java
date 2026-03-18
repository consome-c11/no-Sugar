package com.test.nosugar.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.item.TicUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    //キャッシュ使うとやっぱメモリ使用量とか下がるんかな
    @Unique
    private static final Set<Item> CACHED_AFFECTED_ITEMS = new HashSet<>();
    @Unique
    private static boolean cacheInitialized = false;

    @Unique
    private static final List<String> AFFECTED_ITEM_IDS = new CopyOnWriteArrayList<>(List.of(
            "eraser:sugar_eraser"
    ));

    @Unique
    private static int lastTrackedSlot = -1;
    @Unique
    private static long slotChangeTime = 0L;
    @Unique
    private static final long TRANSITION_DURATION_NS = 500_000_000L;

    @Unique
    private static Item SUGAR_SWORD_ITEM = null;

    @Unique
    private static void initCache() {
        if (cacheInitialized) return;

        if (ModItems.SNACK_HELMET.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_HELMET.get());
        if (ModItems.SNACK_CHESTPLATE.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_CHESTPLATE.get());
        if (ModItems.SNACK_LEGGINGS.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_LEGGINGS.get());
        if (ModItems.SNACK_BOOTS.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.SNACK_BOOTS.get());
        if (ModItems.NULL_INGOT.isPresent()) CACHED_AFFECTED_ITEMS.add(ModItems.NULL_INGOT.get());
        if (ModItems.SUGAR_SWORD.isPresent()) SUGAR_SWORD_ITEM = ModItems.SUGAR_SWORD.get();

        cacheInitialized = true;
    }

    @Unique
    private static boolean shouldAffect(ItemStack stack, ItemDisplayContext ctx) {
        initCache();
        boolean inHand = ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

        boolean issugar = TicUtils.hasSugarMod(stack);
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        issugar = issugar || ModItems.getAllItems().contains(stack.getItem()) ||
                CACHED_AFFECTED_ITEMS.contains(stack.getItem()) ||
                AFFECTED_ITEM_IDS.contains(itemId);

        boolean inGui = ctx == ItemDisplayContext.GUI;
        return (inHand || inGui) && issugar;
    }

    @Unique
    private static boolean isHeldContext(ItemDisplayContext ctx) {
        return ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
                ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||
                ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    @Unique
    private static float getTransitionProgress(long currentTime) {
        long elapsed = currentTime - slotChangeTime;
        if (elapsed >= TRANSITION_DURATION_NS) return 0f;
        float progress = (float) elapsed / TRANSITION_DURATION_NS;
        return 1f - progress * progress;
    }

    @Unique
    private static void updateSlotTracking(Player player) {
        if (player == null) return;
        int currentSlot = player.getInventory().selected;
        long now = System.nanoTime();
        if (currentSlot != lastTrackedSlot) {
            lastTrackedSlot = currentSlot;
            slotChangeTime = now;
        }
    }

    @Unique
    private static boolean isCurrentlySelectedSlotItem(ItemStack stack, Player player) {
        if (player == null) return false;
        ItemStack selected = player.getInventory().getSelected();
        return ItemStack.isSameItemSameTags(stack, selected) && stack.getCount() == selected.getCount();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void eraser$renderHead(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                   PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                   int packedOverlay, BakedModel model, CallbackInfo ci) {
        initCache();
        Minecraft mc = Minecraft.getInstance();
        Player player = (mc != null) ? mc.player : null;

        // Sugar Sword Specific Logic
        if ((context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                && stack.getItem() == SUGAR_SWORD_ITEM) {
            if (player != null && player.isUsingItem() && player.getUseItem() == stack) {
                poseStack.pushPose();
                poseStack.translate(0.0, 0.0, -0.02);
                poseStack.mulPose(Axis.YP.rotationDegrees(60.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(-75.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(25.0F));
                return;
            }
        }

        if (context == ItemDisplayContext.GUI && shouldAffect(stack, context)) {
            updateSlotTracking(player);
            long time = System.nanoTime();
            float transition = getTransitionProgress(time);
            float baseAngle = (float) (Math.sin(time / 500000000.0) * 1.7);
            boolean isSelectedSlot = isCurrentlySelectedSlotItem(stack, player);
            poseStack.pushPose();

            if (isSelectedSlot && transition > 0.01f) {
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
            return;
        }

        // Held Item Logic (Exclude Sugar Sword)
        if (isHeldContext(context) && shouldAffect(stack, context) && stack.getItem() != SUGAR_SWORD_ITEM) {
            long time = System.currentTimeMillis();
            float angle = (float) (Math.sin(time / 500.0) * 10.0);
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void eraser$renderReturn(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                     PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                     int packedOverlay, BakedModel model, CallbackInfo ci) {
        initCache();
        Minecraft mc = Minecraft.getInstance();
        Player player = (mc != null) ? mc.player : null;

        if ((context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                && stack.getItem() == SUGAR_SWORD_ITEM) {
            if (player != null && player.isUsingItem() && player.getUseItem() == stack) {
                poseStack.popPose();
                return;
            }
        }

        if (context == ItemDisplayContext.GUI && shouldAffect(stack, context)) {
            poseStack.popPose();
            return;
        }

        if (isHeldContext(context) && shouldAffect(stack, context) && stack.getItem() != SUGAR_SWORD_ITEM) {
            poseStack.popPose();
        }
    }

    @Unique
    public List<String> getAffectedItemIds() {
        return new CopyOnWriteArrayList<>(AFFECTED_ITEM_IDS);
    }

    @Unique
    public boolean addToAffectedItemIds(String id) {
        return AFFECTED_ITEM_IDS.add(id);
    }
}