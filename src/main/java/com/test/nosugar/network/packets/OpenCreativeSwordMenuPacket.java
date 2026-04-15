package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCreativeSwordMenuPacket {

    public OpenCreativeSwordMenuPacket() {
    }

    public OpenCreativeSwordMenuPacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public static OpenCreativeSwordMenuPacket decode(FriendlyByteBuf buffer) {
        return new OpenCreativeSwordMenuPacket();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            boolean hasCreativeSword = mainHand.getItem() == ModItems.CREATIVE_SWORD.get()
                    || (offHand != null && offHand.getItem() == ModItems.CREATIVE_SWORD.get());

            if (hasCreativeSword) {
                boolean isMainHand = mainHand.getItem() == ModItems.CREATIVE_SWORD.get();
                PacketHandler.sendToPlayer(new OpenCreativeSwordMenuResponsePacket(isMainHand), player);
            }
        });
        context.setPacketHandled(true);
    }
}
