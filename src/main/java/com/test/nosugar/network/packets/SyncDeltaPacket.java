package com.test.nosugar.network.packets;

import com.test.nosugar.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDeltaPacket {
    public final int entityId;
    public final float deltaValue;

    public SyncDeltaPacket(int entityId, float deltaValue) {
        this.entityId = entityId;
        this.deltaValue = deltaValue;
    }

    public static SyncDeltaPacket decode(FriendlyByteBuf buffer) {
        return new SyncDeltaPacket(buffer.readInt(), buffer.readFloat());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeFloat(this.deltaValue);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandler.handleSetDelta(this);
        });
        return true;
    }
}