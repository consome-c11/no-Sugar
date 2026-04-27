package com.test.nosugar.network.packets;

import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandleErasePacket {

    public HandleErasePacket() {
    }

    public static void encode(HandleErasePacket msg, FriendlyByteBuf buf) {
    }

    public static HandleErasePacket decode(FriendlyByteBuf buf) {
        return new HandleErasePacket();
    }

    public static void handle(HandleErasePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if (player instanceof ILivingEntity player_) {
                    //player_.unmarkErased(player.getUUID());
                    //player_.setErased(false);
                }

            }
        });
        ctx.get().setPacketHandled(true);
    }
}
