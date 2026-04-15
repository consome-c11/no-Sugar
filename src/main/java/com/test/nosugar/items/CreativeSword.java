package com.test.nosugar.items;

import com.test.nosugar.additional.ModDamageSources;
import com.test.nosugar.utils.entity.EntityUtils;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;


public class CreativeSword extends Item {
    public static final String TAG_SETTINGS = "CreativeSwordSettings";
    public static final String TAG_LEFT_CLICK = "LeftClickAction"; //0: Kill, 1: Remove
    public static final String TAG_RIGHT_CLICK = "RightClickAction"; //0: KillAll, 1: RemoveAll
    public static final String TAG_INVULNERABLE = "Invulnerable";
    public static final String TAG_AGGRO_IMMUNE = "AggroImmune";

    public CreativeSword(Item.Properties props) {
        super(props);
    }

    private static CompoundTag getOrCreateSettingsTag(ItemStack stack) {
        return stack.getOrCreateTagElement("CreativeSwordSettings");
    }

    public static int getLeftClickAction(ItemStack stack) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        return tag.contains(TAG_LEFT_CLICK) ? tag.getInt(TAG_LEFT_CLICK) : 0;
    }

    public static void setLeftClickAction(ItemStack stack, int value) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        tag.putInt(TAG_LEFT_CLICK, value);
    }

    public static int getRightClickAction(ItemStack stack) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        return tag.contains(TAG_RIGHT_CLICK) ? tag.getInt(TAG_RIGHT_CLICK) : 0;
    }

    public static void setRightClickAction(ItemStack stack, int value) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        tag.putInt(TAG_RIGHT_CLICK, value);
    }

    public static boolean isInvulnerable(ItemStack stack) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        return tag.getBoolean(TAG_INVULNERABLE);
    }

    public static void setInvulnerable(ItemStack stack, boolean value) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        tag.putBoolean(TAG_INVULNERABLE, value);
    }

    public static boolean isAggroImmune(ItemStack stack) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        return tag.getBoolean(TAG_AGGRO_IMMUNE);
    }

    public static void setAggroImmune(ItemStack stack, boolean value) {
        CompoundTag tag = getOrCreateSettingsTag(stack);
        tag.putBoolean(TAG_AGGRO_IMMUNE, value);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        String text = Component.translatable("item.nosugar.creative_sword").getString();
        return makeWaveLine(text, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String desc = Component.translatable("item.nosugar.creative_sword.desc").getString();
        tooltip.add(makeWaveLine(desc, 0xFFAAAAAA, 0xFFFFFFFF));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        int action = getLeftClickAction(stack);
        if (action == 0) {
            if (target instanceof LivingEntity living) Eraser_Utils.killIfParentFound(living, player);
        } else {
            if (target instanceof LivingEntity living) Eraser_Utils.killIfParentFound(living, player, true);
        }
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);

        if (level.isClientSide()) {
            return InteractionResultHolder.fail(itemStack);
        }

        int action = getRightClickAction(itemStack);
        List<Entity> entities = EntityUtils.getEntities((ServerLevel) level);

        if (action == 0) {
            entities.forEach((ent) -> {
                DamageSource src = ModDamageSources.erase(ent, player);
                if (ent instanceof LivingEntity living && living instanceof ILivingEntity iliving && living.getId() != player.getId()) {
                    iliving.instantKill(player, false, src);
                }
            });
        } else {
            entities.forEach((ent) -> {
                DamageSource src = ModDamageSources.erase(ent, player);
                if (ent instanceof LivingEntity living && living instanceof ILivingEntity iliving && living.getId() != player.getId()) {
                    iliving.instantKill(player, true, src);
                }
            });
        }

        return InteractionResultHolder.success(itemStack);
    }
}
