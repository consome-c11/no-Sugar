package com.test.nosugar.items;

import com.test.nosugar.Config;
import com.test.nosugar.additional.ModKeyBindings;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class Halo_of_Sugar_item  extends Item implements ICurioItem {
    private static final Random RANDOM = new Random();

    public Halo_of_Sugar_item(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return "head".equals(slotContext.identifier());
    }

    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack){
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public Component getName(ItemStack stack) {
        return makeWaveLine(Component.translatable("item.nosuger.halo_of_sugar.name").getString(), true);
    }

    @OnlyIn(Dist.CLIENT)
    public static String getRandomSugarLine() {
        String fullText = Component.translatable("item.nosugar.halo.of.sugar.desc").getString();
        String[] lines = fullText.split("\n");
        return lines[RANDOM.nextInt(lines.length)];
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {

        if (Screen.hasShiftDown()) {
            tooltips.add(ColorUtils.makeWaveLine(
                    " " + getRandomSugarLine(), true));
            addActiveBypassTooltips(tooltips, 2);
            tooltips.add(ColorUtils.makeWaveLine(
                    " " + Component.translatable("item.nosugar.halo.of.sugar.stopwatch").getString()
                            .replace("[KEY]", ModKeyBindings.HALO_TIMESTOP.getKey().getDisplayName().getString())));
            tooltips.add(ColorUtils.makeWaveLine(
                    " " + Component.translatable("item.nosugar.halo.of.sugar.canteen").getString()
                            .replace("[KEY]", ModKeyBindings.HALO_STRAGE.getKey().getDisplayName().getString())));
        } else {
            tooltips.add(ColorUtils.makeWaveLine(
                    " " + Component.translatable("item.nosugar.show_advanced").getString()));
        }

        return tooltips;
    }

    @OnlyIn(Dist.CLIENT)
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
                tooltip.add(insertIndex + offset, makeWaveLine(text));
                offset++;
            }
        }
    }

}
