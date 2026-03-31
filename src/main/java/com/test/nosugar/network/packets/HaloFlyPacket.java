package com.test.nosugar.network.packets;

import com.test.nosugar.utils.entity.EntityUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class HaloFlyPacket {
    private final boolean isFlying;

    public HaloFlyPacket(boolean isFlying) {
        this.isFlying = isFlying;
    }

    public static void encode(HaloFlyPacket msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.isFlying);
    }

    public static HaloFlyPacket decode(FriendlyByteBuf buffer) {
        return new HaloFlyPacket(buffer.readBoolean());
    }

    public static void handle(HaloFlyPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            if (!EntityUtils.hasHaloOfSugar(player)) return;

            Abilities abilities = player.getAbilities();
            abilities.mayfly = true;
            abilities.flying = msg.isFlying;

        });
        ctx.setPacketHandled(true);
    }

}