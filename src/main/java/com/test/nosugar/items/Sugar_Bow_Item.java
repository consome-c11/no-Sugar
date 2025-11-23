package com.test.nosugar.items;

import com.test.nosugar.additional.ModEntities;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.entity.HomingArrowEntity;
import com.test.nosugar.utils.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class Sugar_Bow_Item extends BowItem {
    public Sugar_Bow_Item(Properties prop) {
        super(prop);
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        Level level = arrow.level();
        HomingArrowEntity homingArrow = new HomingArrowEntity(ModEntities.HOMING_ARROW.get(), level);
        homingArrow.setOwner(arrow.getOwner());

        homingArrow.copyPosition(arrow);
        homingArrow.setDeltaMovement(arrow.getDeltaMovement());
        homingArrow.setPierceLevel((byte)127);
        homingArrow.setCritArrow(arrow.isCritArrow());
        homingArrow.setBaseDamage(arrow.getBaseDamage());
        homingArrow.setKnockback(arrow.getKnockback());

        return homingArrow;
    }

    @Override
    public Component getName(ItemStack stack) {
        String text = "Sugar Bow";
        var result = Component.empty();
        long time = System.currentTimeMillis() / 50;

        for (int i = 0; i < text.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 5.0);
            result = result.append(Component.literal(String.valueOf(text.charAt(i)))
                    .withStyle(style -> style.withColor(color)));
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        long gameTime = (level != null) ? level.getGameTime() : 0;
        String desc = Component.translatable("item.nosugar.sugar_bow.desc").getString();
        var result = Component.empty();
        long time = System.currentTimeMillis() / 50;

        for (int i = 0; i < desc.length(); i++) {
            int color = ColorUtils.waveGrayWhiteColor(time, i, 5.0);
            result = result.append(Component.literal(String.valueOf(desc.charAt(i)))
                    .withStyle(style -> style.withColor(color)));
        }
        tooltip.add(1, result);
    }

    private static final Predicate<ItemStack> SUGAR_ARROW_ONLY = (p_40683_) -> {
        return p_40683_.is(ModItems.SUGAR_ARROW.get());
    };

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return SUGAR_ARROW_ONLY;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        int useDuration = this.getUseDuration(stack) - timeLeft;
        float pullProgress = BowItem.getPowerForTime(useDuration);

        System.out.println("[SugarBow Debug] useDuration=" + useDuration +
                " pullProgress=" + pullProgress +
                " pulling=" + (entity.isUsingItem() ? 1 : 0));

        super.releaseUsing(stack, level, entity, timeLeft);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 777;
    }

}
