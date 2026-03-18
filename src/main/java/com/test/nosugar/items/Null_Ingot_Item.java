package com.test.nosugar.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class Null_Ingot_Item extends Item {
    public Null_Ingot_Item(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        return makeWaveLine("? Ingot", true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String desc = Component.translatable("item.nosuger.null_ingot.desc").getString();
        tooltip.add(1, makeWaveLine(desc, 0xFFAAAAAA, 0xFFFFFFFF));
    }
}