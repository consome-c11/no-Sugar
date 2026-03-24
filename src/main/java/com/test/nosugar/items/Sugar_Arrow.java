package com.test.nosugar.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class Sugar_Arrow extends ArrowItem {
    public Sugar_Arrow(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        return makeWaveLine("Sugar Arrow", true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, makeWaveLine(Component.translatable("item.nosugar.sugar_arrow.desc").getString()));
    }
}

