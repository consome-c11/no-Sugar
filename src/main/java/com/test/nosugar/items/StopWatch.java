package com.test.nosugar.items;

import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.test.nosugar.utils.render.ColorUtils.makeWaveLine;

public class StopWatch extends Item {

    public StopWatch(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public Component getName(ItemStack stack) {
        return makeWaveLine("Stop Watch[W.I.P]", true);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, makeWaveLine(Component.literal("[Warning] This item is currently under development. It may contain critical bugs :(").getString(), 0xFFFF0000, 0xFFFFFF00));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if (!p_41432_.isClientSide()) {
            toggleTimeStop(p_41432_, p_41433_);
            if (TimeStopManager.isStopped(p_41432_)) p_41433_.displayClientMessage(
                    Component.translatable("item.nosugar.stopwatch.stopped"),
                    true
            );
            else p_41433_.displayClientMessage(
                    Component.translatable("item.nosugar.stopwatch.resumed"),
                    true
            );
        }
        return InteractionResultHolder.success(p_41433_.getItemInHand(p_41434_));
    }

    private void toggleTimeStop(Level level, Player player) {
        boolean newState = !TimeStopManager.isStopped(level);
        TimeStopManager.setStopped(level, newState);

        if (newState) {
            TimeStopManager.addEntity(player);
        } else {
            TimeStopManager.removeEntity(player);
        }
    }

}
