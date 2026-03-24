package com.test.nosugar.network.packets;

import com.test.nosugar.utils.BagManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenHaloStragePacket {

    public OpenHaloStragePacket() {
    }

    public OpenHaloStragePacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {}

    public static OpenHaloStragePacket decode(FriendlyByteBuf buffer) {
        return new OpenHaloStragePacket();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BagManager.OpenBag(player, player.getUUID());
            }
        });
        context.setPacketHandled(true);
    }
}