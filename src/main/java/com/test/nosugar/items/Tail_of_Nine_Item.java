package com.test.nosugar.items;

import com.test.nosugar.additional.ModTiers;
import com.test.nosugar.utils.Tail_of_Nine_Handler;
import com.test.nosugar.utils.render.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class Tail_of_Nine_Item extends SwordItem {

    public Tail_of_Nine_Item(Properties props) {
        super(ModTiers.TAIL_OF_NINE_TIER, 10, 3.F, props.stacksTo(1).fireResistant());
    }

    @Override
    public Component getName(ItemStack stack) {
        return ColorUtils.makeWaveLine("Tail of Nine", 0xFF0000, 0xFFFFFF);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        long gameTime = (level != null) ? level.getGameTime() : 0;

    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide() && attacker instanceof Player) {
            Tail_of_Nine_Handler.applyHit(target, attacker);
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}