package com.test.nosugar.utils.item;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;

public class BlessingUtils {

    public enum ItemType {
        SWORD,
        TOOL//ピッケル 斧 シャベル
    }

    public static boolean hasBlessedItem(ItemType type) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.player == null) {
            return false;
        }

        return isBlessedAndMatchesType(mc.player.getMainHandItem(), type);
    }

    public static boolean hasBlessedItem(ItemType type, boolean offhand) {
        Minecraft mc = Minecraft.getInstance();

        if (mc == null || mc.player == null) {
            return false;
        }
        boolean isBlessed = isBlessedAndMatchesType(mc.player.getMainHandItem(), type);
        if(offhand) {
            return isBlessed ||
                    isBlessedAndMatchesType(mc.player.getOffhandItem(), type);
        }
        else return isBlessed;
    }

    public static boolean isBlessedAndMatchesType(ItemStack stack, ItemType type) {
        if (stack.isEmpty()) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("Blessing_of_Sugar")) {
            return false;
        }

        return switch (type) {
            case SWORD -> stack.getItem() instanceof SwordItem;
            case TOOL -> stack.getItem() instanceof PickaxeItem ||
                    stack.getItem() instanceof AxeItem ||
                    stack.getItem() instanceof ShovelItem;
        };
    }

    public static boolean isBlessed(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("Blessing_of_Sugar")) {
            return false;
        }

        return true;
    }

    public static boolean hasBlessedItem(ServerPlayer player, ItemType type) {
        if (player == null) {
            return false;
        }

        return isBlessedAndMatchesType(player.getMainHandItem(), type);
    }

    public static boolean hasBlessedItem(ServerPlayer player, ItemType type, boolean offhand) {

        if (player == null) {
            return false;
        }
        boolean isBlessed = isBlessedAndMatchesType(player.getMainHandItem(), type);
        if(offhand) {
            return isBlessed ||
                    isBlessedAndMatchesType(player.getOffhandItem(), type);
        }
        else return isBlessed;
    }
}