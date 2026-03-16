package com.test.nosugar.gui.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.mixin.client.ClientTextTooltipAccessor;
import com.test.nosugar.utils.item.TicUtils;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SugarToolTipRenderer {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        ItemStack stack = event.getItemStack();
        if (!shouldAffect(stack)) {
            return;
        }

        event.setCanceled(true);

        List<ClientTooltipComponent> components = event.getComponents();

        renderCustomTooltip(
                event.getGraphics(),
                event.getFont(),
                event.getX(),
                event.getY(),
                event.getScreenWidth(),
                event.getScreenHeight(),
                components,
                stack
        );
    }
    private static final List<RegistryObject<Item>> AFFECTED_ITEMS = List.of(
            ModItems.SNACK_HELMET,
            ModItems.SNACK_CHESTPLATE,
            ModItems.SNACK_LEGGINGS,
            ModItems.SNACK_BOOTS,
            ModItems.NULL_INGOT
    );
    private static List<Item> getAffectedItems() {
        return AFFECTED_ITEMS.stream()
                .filter(RegistryObject::isPresent)
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
    private static final List<String> AFFECTED_ITEM_IDS = List.of(
            "eraser:sugar_eraser"
    );
    private static boolean shouldAffect(ItemStack stack) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        List<Item> currentAffectedItems = getAffectedItems();
        boolean issugar = (ModItems.getAllItems().stream().anyMatch(stack::is) ||
                currentAffectedItems.stream().anyMatch(stack::is) || AFFECTED_ITEM_IDS.contains(itemId));

        return issugar;
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
        PoseStack poseStack = guiGraphics.pose();
        poseStack.translate(0, 0, 500);
        poseStack.pushPose();

        int maxWidth = 0;
        int totalHeight = 0;
        int size = components.size();
        //飛ばさないと余分な空白ができる
        for (int i = 0; i < size; i++) {
            ClientTooltipComponent comp = components.get(i);
            int width = comp.getWidth(font);
            int height = comp.getHeight();
            if (width > maxWidth) maxWidth = width;
            if(i < size - 2)totalHeight += height;
        }
        int tooltipWidth = maxWidth + 8;
        int tooltipHeight = totalHeight + 8;

        if (x + tooltipWidth > screenWidth) x = screenWidth - tooltipWidth;
        if (y + tooltipHeight > screenHeight) y = screenHeight - tooltipHeight;
        x = Math.max(4, x);
        y = Math.max(4, y);

        long time = System.currentTimeMillis() / 70;
        double speed = 7.0;


        //TODO:　αを良い感じに調整する
        int alpha = 100;
        int baseTop = ColorUtils.waveGrayWhiteColor(time, x, y - 3, speed);
        int gradientTop = (baseTop & 0x00FFFFFF) | (alpha << 24);

        int baseBottom = ColorUtils.waveGrayWhiteColor(time, x, y + tooltipHeight + 3, speed);
        int gradientBottom = (baseBottom & 0x00FFFFFF) | (alpha << 24);

        guiGraphics.fillGradient(x - 3, y - 4, x + tooltipWidth + 3, y - 3, gradientTop, gradientTop);
        guiGraphics.fillGradient(x - 3, y + tooltipHeight + 3, x + tooltipWidth + 3, y + tooltipHeight + 4, gradientBottom, gradientBottom);
        guiGraphics.fillGradient(x - 3, y - 3, x + tooltipWidth + 3, y + tooltipHeight + 3, gradientTop, gradientBottom);
        guiGraphics.fillGradient(x - 4, y - 3, x - 3, y + tooltipHeight + 3, gradientTop, gradientBottom);
        guiGraphics.fillGradient(x + tooltipWidth + 3, y - 3, x + tooltipWidth + 4, y + tooltipHeight + 3, gradientTop, gradientBottom);

        int borderLeftX = x - 4;
        int borderRightX = x + tooltipWidth + 4;
        int borderTopY = y - 4;
        int borderBottomY = y + tooltipHeight + 3;
        int outerTopY = y - 3;
        int outerBottomY = y + tooltipHeight + 3;

        guiGraphics.fillGradient(borderLeftX, borderTopY, borderRightX, borderTopY + 1,
                ColorUtils.waveGrayWhiteColor(time, borderLeftX, borderTopY, speed),
                ColorUtils.waveGrayWhiteColor(time, borderRightX, borderTopY, speed));
        guiGraphics.fillGradient(borderLeftX, borderBottomY, borderRightX, borderBottomY + 1,
                ColorUtils.waveGrayWhiteColor(time, borderLeftX, borderBottomY, speed),
                ColorUtils.waveGrayWhiteColor(time, borderRightX, borderBottomY, speed));

        guiGraphics.fillGradient(borderLeftX, outerTopY, borderLeftX + 1, outerBottomY,
                ColorUtils.waveGrayWhiteColor(time, borderLeftX, outerTopY, speed),
                ColorUtils.waveGrayWhiteColor(time, borderLeftX, outerBottomY, speed));
        guiGraphics.fillGradient(borderRightX, outerTopY, borderRightX + 1, outerBottomY,
                ColorUtils.waveGrayWhiteColor(time, borderRightX, outerTopY, speed),
                ColorUtils.waveGrayWhiteColor(time, borderRightX, outerBottomY, speed));
        float amplitude = 0.0f;
        int offsetY = y;
        int renderLimit = Math.max(0, components.size() - 1);
        for (int i = 0; i < renderLimit; i++) {
            ClientTooltipComponent comp = components.get(i);
            if (comp instanceof ClientTextTooltip textTooltip) {
                renderWavingText(guiGraphics, font, textTooltip, x, offsetY, time, (float) speed, amplitude);
            } else {
                comp.renderImage(font, x, offsetY, guiGraphics);
            }
            offsetY += comp.getHeight();
        }

        poseStack.popPose();
    }

    private static void renderWavingText(
            GuiGraphics guiGraphics,
            Font font,
            ClientTextTooltip tooltip,
            int x,
            int y,
            long time,
            double speed,
            double amplitude
    ) {
        FormattedCharSequence text = ((ClientTextTooltipAccessor) (Object) tooltip).getText();
        if (text == null) return;

        PoseStack poseStack = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        List<CharData> chars = new ArrayList<>();
        text.accept((idx, style, code) -> {
            chars.add(new CharData(code, style));
            return true;
        });

        float currentX = x;
        float baseY = y;

        for (int i = 0; i < chars.size(); i++) {
            CharData data = chars.get(i);
            String ch = new String(Character.toChars(data.code));
            int charWidth = font.width(ch);

            float offset = (float)(Math.sin((time * 0.05f) + (i * 0.3f)) * amplitude);

            poseStack.pushPose();
            poseStack.translate(0, offset, 0);

            MutableComponent component = Component.literal(ch).setStyle(data.style);
            font.drawInBatch(component, currentX, baseY, 0xFFFFFFFF, true,
                    poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 15728880);

            poseStack.popPose();
            currentX += charWidth;
        }
        buffer.endBatch();
    }

    private static class CharData {
        int code;
        Style style;
        CharData(int code, Style style) {
            this.code = code;
            this.style = style;
        }
    }
}