package com.test.nosugar.utils;

import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.world.entity.LivingEntity;

public class Tail_of_Nine_Handler {
    private static final String NBT_KEY_COUNT = "nosugar:tail_of_nine_count";
    private static final int MAX_HITS = 9;

    public static boolean applyHit(LivingEntity target, LivingEntity attacker) {
        if (target.level().isClientSide()) {
            return false;
        }

        var data = target.getPersistentData();
        int count = data.getInt(NBT_KEY_COUNT) + 1;
        data.putInt(NBT_KEY_COUNT, count);

        if (count >= MAX_HITS) {
            Eraser_Utils.killIfParentFound(target, attacker);
            return true;
        }
        return false;
    }

    public static void resetCount(LivingEntity target) {
        target.getPersistentData().remove(NBT_KEY_COUNT);
    }
}