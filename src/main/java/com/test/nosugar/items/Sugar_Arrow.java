package com.test.nosugar.items;

import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Sugar_Arrow extends ArrowItem {
    public Sugar_Arrow(Properties props) {
        super(props);
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Sugar Arrow";
        var result = Component.empty();
        long time = System.currentTimeMillis() / 50;

        for (int i = 0; i < text.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 6.0);
            result = result.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(style -> style.withColor(color)));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        long gameTime = (level != null) ? level.getGameTime() : 0;
        String desc = Component.translatable("item.nosugar.sugar_arrow.desc").getString();
        var result = Component.empty();
        long time = System.currentTimeMillis() / 50;

        for (int i = 0; i < desc.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 6.0);
            result = result.append(Component.literal(String.valueOf(desc.charAt(i)))
                    .withStyle(style -> style.withColor(color)));
        }
        tooltip.add(1, result);
    }
}

