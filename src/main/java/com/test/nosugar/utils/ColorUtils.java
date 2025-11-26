package com.test.nosugar.utils;

import com.test.nosugar.NoSugar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ColorUtils {
    public static int waveGrayWhiteColor(long time, int index, double speed) {
        double wave = (Math.sin((time / speed) + index) + 1.0) / 2.0;

        int gray = 0xAAAAAA;
        int white = 0xFFFFFF;

        int r = (int) (((gray >> 16) & 0xFF) * (1 - wave) + ((white >> 16) & 0xFF) * wave);
        int g = (int) (((gray >> 8) & 0xFF) * (1 - wave) + ((white >> 8) & 0xFF) * wave);
        int b = (int) ((gray & 0xFF) * (1 - wave) + (white & 0xFF) * wave);

        return (r << 16) | (g << 8) | b;
    }

    public static MutableComponent makeWaveLine(String text) {
        long time = System.currentTimeMillis() / 50;
        MutableComponent waveLine = Component.empty();
        for (int i = 0; i < text.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 6.0);
            waveLine = waveLine.append(
                    Component.literal(String.valueOf(text.charAt(i)))
                            .withStyle(s -> s.withColor(color))
            );
        }
        return waveLine;
    }

    public static float[] getGlintColor(ItemStack stack) {
       // String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        //if (itemId.contains(NoSugar.MODID)) {
            return new float[]{1.0F, 1.0F, 1.0F};
        //}
        //return null;
    }
}
