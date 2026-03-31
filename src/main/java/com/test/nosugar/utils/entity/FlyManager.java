package com.test.nosugar.utils.entity;

public class FlyManager {
    private static boolean canDisableFly = false;
    public static void setCanDisableFly(boolean can) {
        canDisableFly = can;
    }
    public static boolean isCanDisableFly() {
        return canDisableFly;
    }
}
