package com.test.nosugar.utils.render;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ColorUtils {


    public static int waveColor(long time, int index, double speed, int startColor, int endColor) {
        double wave = (Math.sin((time / speed) + index) + 1.0) / 2.0;

        int r = (int) (((startColor >> 16) & 0xFF) * (1 - wave) + ((endColor >> 16) & 0xFF) * wave);
        int g = (int) (((startColor >> 8) & 0xFF) * (1 - wave) + ((endColor >> 8) & 0xFF) * wave);
        int b = (int) ((startColor & 0xFF) * (1 - wave) + (endColor & 0xFF) * wave);

        return (r << 16) | (g << 8) | b;
    }

    public static int waveGrayWhiteColor(long time, int index, double speed) {
        return waveColor(time, index, speed, 0xAAAAAA, 0xFFFFFF);
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

    public static MutableComponent makeWaveLine(String text, int startColor, int endColor) {
        long time = System.currentTimeMillis() / 50;
        MutableComponent waveLine = Component.empty();
        for (int i = 0; i < text.length(); i++) {
            int color = ColorUtils.waveColor(time, i, 6.0, startColor, endColor); // Wave speed
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
