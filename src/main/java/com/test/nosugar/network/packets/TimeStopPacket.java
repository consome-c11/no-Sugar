package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.TimeStopManager;
import com.test.nosugar.utils.entity.EntityUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TimeStopPacket {

    public TimeStopPacket() {}

    public TimeStopPacket(FriendlyByteBuf buffer) {}

    public static TimeStopPacket decode(FriendlyByteBuf buffer) {
        return new TimeStopPacket();
    }
    public void encode(FriendlyByteBuf buffer) {}

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            Level level = player.level();

            boolean hasHalo = EntityUtils.hasHaloOfSugar(player);

            boolean hasStopWatch = player.getInventory().contains(new ItemStack(ModItems.STOP_WATCH.get()))
                    || player.getOffhandItem().is(ModItems.STOP_WATCH.get());

            if (hasHalo || hasStopWatch) {
                boolean isCurrentlyStopped = TimeStopManager.isStopped(level);

                if (!isCurrentlyStopped) {
                    TimeStopManager.startStop(level, player);
                    TimeStopManager.addEntity(player);
                    player.displayClientMessage(Component.translatable("item.nosugar.stopwatch.stopped"), true);
                } else {
                    TimeStopManager.Resume(level, player);
                    TimeStopManager.clearEntity(level);
                    player.displayClientMessage(Component.translatable("item.nosugar.stopwatch.resumed"), true);
                }
            }
        });
        context.setPacketHandled(true);
    }
}