package com.test.nosugar.network.packets;

import com.test.nosugar.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record EraseEntityPacket(UUID entityUuid, boolean skipAnimation) {

    public static void encode(EraseEntityPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.entityUuid);
        buf.writeBoolean(msg.skipAnimation);
    }

    public static EraseEntityPacket decode(FriendlyByteBuf buf) {
        return new EraseEntityPacket(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(EraseEntityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (FMLEnvironment.dist.isClient()) {
                //System.out.println("Received EraseEntityPacket for UUID: " + msg.entityUuid);

                ClientPacketHandler.handleEraseEntity(msg);
            }
        });
        context.setPacketHandled(true);
    }
}
