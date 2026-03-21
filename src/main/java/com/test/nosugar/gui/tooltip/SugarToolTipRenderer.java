package com.test.nosugar.gui.tooltip;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.mixin.client.ClientTextTooltipAccessor;
import com.test.nosugar.mixin.client.FontAccessor;
import com.test.nosugar.utils.render.ColorUtils;
import com.test.nosugar.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SugarToolTipRenderer {

    private static volatile Set<Item> cachedAffectedItems = null;

    private static final Set<String> AFFECTED_ITEM_IDS = Set.of(
            "eraser:sugar_eraser"
    );

    private static final List<RegistryObject<Item>> AFFECTED_REGISTRY_OBJECTS = List.of(
            ModItems.SNACK_HELMET,
            ModItems.SNACK_CHESTPLATE,
            ModItems.SNACK_LEGGINGS,
            ModItems.SNACK_BOOTS,
            ModItems.NULL_INGOT
    );

    private static Set<Item> buildCache() {
        Set<Item> set = new HashSet<>();
        for (RegistryObject<Item> ro : AFFECTED_REGISTRY_OBJECTS) {
            if (ro.isPresent()) set.add(ro.get());
        }
        set.addAll(ModItems.getAllItems());
        return Collections.unmodifiableSet(set);
    }

    private static Set<Item> getAffectedItemsCache() {
        Set<Item> local = cachedAffectedItems;
        if (local == null) {
            synchronized (SugarToolTipRenderer.class) {
                local = cachedAffectedItems;
                if (local == null) {
                    local = buildCache();
                    cachedAffectedItems = local;
                }
            }
        }
        return local;
    }

    public static void invalidateCache() {
        cachedAffectedItems = null;
    }

    private static boolean shouldAffect(ItemStack stack) {
        Item item = stack.getItem();
        if (getAffectedItemsCache().contains(item)) return true;
        String itemId = BuiltInRegistries.ITEM.getKey(item).toString();
        return AFFECTED_ITEM_IDS.contains(itemId);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        ItemStack stack = event.getItemStack();
        if (!shouldAffect(stack)) return;

        event.setCanceled(true);

        renderCustomTooltip(
                event.getGraphics(),
                event.getFont(),
                event.getX(),
                event.getY(),
                event.getScreenWidth(),
                event.getScreenHeight(),
                event.getComponents(),
                stack
        );
    }

    private static void renderCustomTooltip(
            GuiGraphics guiGraphics,
            Font font,
            int x,
            int y,
            int screenWidth,
            int screenHeight,
            List<ClientTooltipComponent> components,
            ItemStack stack
    ) {
        x += 10;
        y += 10;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 500);

        int maxWidth = 0;
        int totalHeight = 0;
        int renderLimit = Math.max(0, components.size());

        for (int i = 0; i < renderLimit; i++) {
            ClientTooltipComponent comp = components.get(i);
            int width = comp.getWidth(font);
            int height = comp.getHeight();
            if (width > maxWidth) maxWidth = width;
            if(comp.getWidth(font) > font.width(""))totalHeight += height;
        }

        int tooltipWidth = maxWidth + 8;
        int tooltipHeight = totalHeight + 8;

        if (x + tooltipWidth > screenWidth) x = screenWidth - tooltipWidth;
        if (y + tooltipHeight > screenHeight) y = screenHeight - tooltipHeight;
        x = Math.max(4, x);
        y = Math.max(4, y);

        double timeSec = System.currentTimeMillis() * 0.001;
        long colorTime = System.currentTimeMillis() / 70;
        double colorSpeed = 7.0;

        int alpha = 100;
        int baseTop = ColorUtils.waveGrayWhiteColor(colorTime, x, y - 3, colorSpeed);
        int gradientTop = (baseTop & 0x00FFFFFF) | (alpha << 24);
        int baseBottom = ColorUtils.waveGrayWhiteColor(colorTime, x, y + tooltipHeight + 3, colorSpeed);
        int gradientBottom = (baseBottom & 0x00FFFFFF) | (alpha << 24);

        guiGraphics.fillGradient(x - 3, y - 4, x + tooltipWidth + 3, y - 3, gradientTop, gradientTop);
        guiGraphics.fillGradient(x - 3, y + tooltipHeight + 3, x + tooltipWidth + 3, y + tooltipHeight + 4, gradientBottom, gradientBottom);
        guiGraphics.fillGradient(x - 3, y - 3, x + tooltipWidth + 3, y + tooltipHeight + 3, gradientTop, gradientBottom);
        guiGraphics.fillGradient(x - 4, y - 3, x - 3, y + tooltipHeight + 3, gradientTop, gradientBottom);
        guiGraphics.fillGradient(x + tooltipWidth + 3, y - 3, x + tooltipWidth + 4, y + tooltipHeight + 3, gradientTop, gradientBottom);

        int bL = x - 4, bR = x + tooltipWidth + 4;
        int bT = y - 4, bB = y + tooltipHeight + 3;
        int oT = y - 3, oB = y + tooltipHeight + 3;

        guiGraphics.fillGradient(bL, bT, bR, bT + 1,
                ColorUtils.waveGrayWhiteColor(colorTime, bL, bT, colorSpeed),
                ColorUtils.waveGrayWhiteColor(colorTime, bR, bT, colorSpeed));
        guiGraphics.fillGradient(bL, bB, bR, bB + 1,
                ColorUtils.waveGrayWhiteColor(colorTime, bL, bB, colorSpeed),
                ColorUtils.waveGrayWhiteColor(colorTime, bR, bB, colorSpeed));
        guiGraphics.fillGradient(bL, oT, bL + 1, oB,
                ColorUtils.waveGrayWhiteColor(colorTime, bL, oT, colorSpeed),
                ColorUtils.waveGrayWhiteColor(colorTime, bL, oB, colorSpeed));
        guiGraphics.fillGradient(bR, oT, bR + 1, oB,
                ColorUtils.waveGrayWhiteColor(colorTime, bR, oT, colorSpeed),
                ColorUtils.waveGrayWhiteColor(colorTime, bR, oB, colorSpeed));

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        int offsetY = y;

        for (int i = 0; i < renderLimit; i++) {
            ClientTooltipComponent comp = components.get(i);
            if (comp instanceof ClientTextTooltip textTooltip) {
                textTooltip.renderText(font, x, offsetY, poseStack.last().pose(), buffer);
            } else {
                comp.renderImage(font, x, offsetY, guiGraphics);
            }
            offsetY += comp.getHeight();
        }

        buffer.endBatch();
        poseStack.popPose();
    }

}