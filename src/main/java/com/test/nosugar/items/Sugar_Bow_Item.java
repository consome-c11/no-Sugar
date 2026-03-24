package com.test.nosugar.items;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.entity.HomingArrowEntity;
import com.test.nosugar.entity.ModEntities;
import com.test.nosugar.utils.ShootMode;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class Sugar_Bow_Item extends BowItem {
    private static final Predicate<ItemStack> SUGAR_ARROW_ONLY = (p_40683_) -> {
        return p_40683_.is(ModItems.SUGAR_ARROW.get());
    };
    public static ItemStack THIS;

    public Sugar_Bow_Item(Properties prop) {
        super(prop);
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow) {
        Level level = arrow.level();
        HomingArrowEntity homingArrow = new HomingArrowEntity(ModEntities.HOMING_ARROW.get(), level, ShootMode.getMode(THIS) == ShootMode.EXPLOSIVES);
        homingArrow.setOwner(arrow.getOwner());

        homingArrow.copyPosition(arrow);
        homingArrow.setDeltaMovement(arrow.getDeltaMovement());
        homingArrow.setPierceLevel((byte) 127);
        homingArrow.setCritArrow(arrow.isCritArrow());
        homingArrow.setBaseDamage(arrow.getBaseDamage());
        homingArrow.setKnockback(arrow.getKnockback());

        return homingArrow;
    }

    @Override
    public Component getName(ItemStack stack) {
        return makeWaveLine("Sugar Bow", true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, makeWaveLine(Component.translatable("item.nosugar.sugar_bow.desc").getString()));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int useDuration = this.getUseDuration(stack) - timeLeft;
            float pullProgress = BowItem.getPowerForTime(useDuration);
            int arrowamount = ShootMode.getMode(stack) == ShootMode.MULTI ? 16 : 1;
            if (pullProgress >= 0.1F) {

                for (int i = 0; i < arrowamount; i++) {

                    Arrow vanillaArrow = new Arrow(level, player);
                    THIS = stack;
                    AbstractArrow customArrow = this.customArrow(vanillaArrow);

                    customArrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3, 1.0F);

                    level.addFreshEntity(customArrow);
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + pullProgress * 0.5F);

                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return SUGAR_ARROW_ONLY;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        /*if (player.isShiftKeyDown() && !level.isClientSide) {
            //PacketHandler.CHANNEL.sendToServer(new SugarBowSetModePacket(ShootMode.cycleMode(stack)));
            ShootMode.cycleMode(stack);
            return InteractionResultHolder.pass(stack);
        }*/

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
    }//:/

}
