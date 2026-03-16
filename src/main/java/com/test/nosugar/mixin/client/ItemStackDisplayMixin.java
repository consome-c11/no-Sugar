package com.test.nosugar.mixin.client;

import com.test.nosugar.Config;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.DestroyMode;
import com.test.nosugar.utils.ShootMode;
import com.test.nosugar.utils.item.BlessingUtils;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackDisplayMixin {

    private static MutableComponent makeWaveLine(String text, boolean grayWhite) {
        long time = System.currentTimeMillis() / 50;
        MutableComponent waveLine = Component.empty();
        for (int i = 0; i < text.length(); i++) {
            int color = grayWhite
                    ? ColorUtils.waveGrayWhiteColor(time, i, 6.0)
                    : waveYellowGoldColor(time, i, 6.0);
            waveLine = waveLine.append(
                    Component.literal(String.valueOf(text.charAt(i)))
                            .withStyle(s -> s.withColor(color))
            );
        }
        return waveLine;
    }

    private static Component buildInfinityLine(Component attributeName) {
        long time = System.currentTimeMillis() / 50;
        String text = " Infinity " + attributeName.getString();
        MutableComponent waveLine = Component.empty();
        for (int j = 0; j < text.length(); j++) {
            int color = ColorUtils.waveGrayWhiteColor(time, j, 6.0);
            waveLine = waveLine.append(
                    Component.literal(String.valueOf(text.charAt(j)))
                            .withStyle(s -> s.withColor(color))
            );
        }
        return Component.literal("").append(waveLine);
    }

    private static int waveYellowGoldColor(long time, int index, double speed) {
        double wave = (Math.sin((time / speed) + index) + 1.0) / 2.0;
        int yellow = 0xFFFF55;
        int gold = 0xFFAA00;
        int r = (int) (((yellow >> 16) & 0xFF) * (1 - wave) + ((gold >> 16) & 0xFF) * wave);
        int g = (int) (((yellow >> 8) & 0xFF) * (1 - wave) + ((gold >> 8) & 0xFF) * wave);
        int b = (int) ((yellow & 0xFF) * (1 - wave) + (gold & 0xFF) * wave);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void injectTooltip(@Nullable Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
        if (player == null) return;
        List<Component> tooltip = cir.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;

        boolean isEraserOrWorld = stack.getItem() == ModItems.SUGAR_SWORD.get() || stack.getItem() == ModItems.WORLD_DESTROYER.get();
        boolean isSnackProtector = stack.getItem() == ModItems.SNACK_BOOTS.get() || stack.getItem() == ModItems.SNACK_LEGGINGS.get() || stack.getItem() == ModItems.SNACK_CHESTPLATE.get() || stack.getItem() == ModItems.SNACK_HELMET.get();
        if(isEraserOrWorld || stack.getItem() == ModItems.TAIL_OF_NINE.get()) {
            String mainhandKeyStr = Component.translatable("item.modifiers.mainhand").getString();
            for (int i = 0; i < tooltip.size(); i++) {
                Component line = tooltip.get(i);
                String lineStr = line.getString();

                if (lineStr.contains(mainhandKeyStr)) {
                    if (Screen.hasShiftDown()) {
                        addActiveBypassTooltips(tooltip, i + 1);
                        if(stack.getItem() == ModItems.TAIL_OF_NINE.get())
                            tooltip.add(i + 1, ColorUtils.makeWaveLine(" " + Component.translatable("item.nosugar.tail.of.nine.desc").getString(), 0xFF0000, 0xFFFFFFD));
                        if(stack.getItem() == ModItems.SUGAR_SWORD.get())
                            tooltip.add(i + 1, ColorUtils.makeWaveLine(" " + Component.translatable("item.nosugar.sugar.sword.desc").getString()));

                    } else {
                        tooltip.add(i + 1, makeWaveLine(" " + Component.translatable("item.nosugar.show_advanced").getString(), true));
                        if(stack.getItem() == ModItems.TAIL_OF_NINE.get()) tooltip.add(i + 1, ColorUtils.makeWaveLine(" " + "Tail of Nine", 0xFF0000, 0xFFFFFFD));
                        if(stack.getItem() == ModItems.SUGAR_SWORD.get()) tooltip.add(i + 1, ColorUtils.makeWaveLine(" " + "Sugar"));
                    }

                    break;
                }
            }
        }
        if (isEraserOrWorld || isSnackProtector || BlessingUtils.isBlessed(stack)) {
            if (BlessingUtils.isBlessed(stack)) {
                tooltip.add(makeWaveLine("Sugar Blessing", true));
            }
            String attackKeyStr = Component.translatable("attribute.name.generic.attack_damage").getString();
            String armorKeyStr = Component.translatable("attribute.name.generic.armor").getString();
            String toughnessKeyStr = Component.translatable("attribute.name.generic.armor_toughness").getString();

            for (int i = 0; i < tooltip.size(); i++) {
                Component line = tooltip.get(i);
                String lineStr = line.getString();

                if (lineStr.contains(attackKeyStr)) {
                    Component attrComp = Component.translatable("attribute.name.generic.attack_damage");
                    if (!BlessingUtils.isBlessedAndMatchesType(stack, BlessingUtils.ItemType.TOOL))
                        tooltip.set(i, buildInfinityLine(attrComp));
                } else if (lineStr.contains(armorKeyStr)) {
                    Component attrComp = Component.translatable("attribute.name.generic.armor");
                    tooltip.set(i, buildInfinityLine(attrComp));
                } else if (lineStr.contains(toughnessKeyStr)) {
                    Component attrComp = Component.translatable("attribute.name.generic.armor_toughness");
                    tooltip.set(i, buildInfinityLine(attrComp));
                }
            }
            if (stack.getItem() == ModItems.SUGAR_SWORD.get()) {

                tooltip.add(Component.translatable("item.erasers.use")
                        .withStyle(ChatFormatting.GRAY));

                String normalText = " Shot Homing Arrow";//removed
                //tooltip.add(makeWaveLine(normalText, true));

                tooltip.add(Component.translatable("item.erasers.sneak_use")
                        .withStyle(ChatFormatting.DARK_GRAY));

                String sneakText = " RangeAttack";
                tooltip.add(makeWaveLine(sneakText, true));
            }

            if (stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
                tooltip.add(Component.translatable("item.erasers.use")
                        .withStyle(ChatFormatting.GRAY));

                String normalText = " Get Cookie x1";
                tooltip.add(makeWaveLine(normalText, false));

                tooltip.add(Component.translatable("item.erasers.sneak_use")
                        .withStyle(ChatFormatting.DARK_GRAY));

                String sneakText = " Get Cookie x64";
                tooltip.add(makeWaveLine(sneakText, false));
                //なんか表示されんかったんやﾕﾙｼﾃ:sob:
                String sneakText2 = "";
                tooltip.add(makeWaveLine(sneakText, false));
            }

            cir.setReturnValue(tooltip);
        }
    }

    @Unique
    private void addActiveBypassTooltips(List<Component> tooltip, int insertIndex) {
        Map<TagKey<DamageType>, String> tagToKeyMap = Map.ofEntries(
                Map.entry(DamageTypeTags.BYPASSES_INVULNERABILITY, "item.nosugar.ignore.invulnerability"),
                Map.entry(DamageTypeTags.BYPASSES_ARMOR, "item.nosugar.ignore.armor"),
                Map.entry(DamageTypeTags.BYPASSES_SHIELD, "item.nosugar.ignore.shield"),
                Map.entry(DamageTypeTags.BYPASSES_ENCHANTMENTS, "item.nosugar.ignore.enchantments"),
                Map.entry(DamageTypeTags.BYPASSES_EFFECTS, "item.nosugar.ignore.effects"),
                Map.entry(DamageTypeTags.BYPASSES_COOLDOWN, "item.nosugar.ignore.cooldown")
        );

        int offset = 0;
        for (Map.Entry<TagKey<DamageType>, String> entry : tagToKeyMap.entrySet()) {
            if (Config.shouldBypassTag(entry.getKey())) {
                String text = " " + Component.translatable(entry.getValue()).getString();
                tooltip.add(insertIndex + offset, makeWaveLine(text, true));
                offset++;
            }
        }
    }

    @Unique
    private boolean applycolorname(ItemStack stack) {//hate my brain
        return stack.getItem() == ModItems.SUGAR_SWORD.get() || stack.getItem() == ModItems.WORLD_DESTROYER.get() || stack.getItem() == ModItems.SNACK_BOOTS.get() || stack.getItem() == ModItems.SNACK_LEGGINGS.get() || stack.getItem() == ModItems.SNACK_CHESTPLATE.get() || stack.getItem() == ModItems.SNACK_HELMET.get() || stack.getItem() == ModItems.SUGAR_BOW.get();
    }

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void injectName(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = (ItemStack) (Object) this;

        if (applycolorname(stack)) {
            String text = cir.getReturnValue().getString(); // after rename
            long time = System.currentTimeMillis() / 50;

            if (stack.getItem() == ModItems.WORLD_DESTROYER.get()) {
                text += " Mode:[";
                text += DestroyMode.getMode(stack);
                text += "]";
                if (DestroyMode.isSilkTouchEnabled(stack)) {
                    text += " [SilkTouch Enabled]";
                }
            } else if (stack.getItem() == ModItems.SUGAR_BOW.get()) {
                text += " Mode:[";
                text += ShootMode.getMode(stack).getDisplayName();
                text += "]";
            }

            MutableComponent waveLine = Component.empty();
            for (int i = 0; i < text.length(); i++) {
                int color = ColorUtils.waveGrayWhiteColor(time, i, 6.0);
                waveLine = waveLine.append(
                        Component.literal(String.valueOf(text.charAt(i)))
                                .withStyle(s -> s.withColor(color))
                );
            }

            cir.setReturnValue(waveLine);
        }
    }
}

