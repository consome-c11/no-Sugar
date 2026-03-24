package com.test.nosugar.items;

import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.TimeStopPacket;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.level().isClientSide)PacketHandler.CHANNEL.sendToServer(new TimeStopPacket());

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

}
