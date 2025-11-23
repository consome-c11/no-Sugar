package com.test.nosugar.utils;

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
}
