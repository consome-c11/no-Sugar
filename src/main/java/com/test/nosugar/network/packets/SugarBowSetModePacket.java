package com.test.nosugar.network.packets;

import com.test.nosugar.items.Sugar_Bow_Item;
import com.test.nosugar.utils.ShootMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SugarBowSetModePacket {

    private final int modeOrdinal;

    public SugarBowSetModePacket(ShootMode mode) {
        this.modeOrdinal = mode.ordinal();
    }

    public static void encode(SugarBowSetModePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.modeOrdinal);
    }

    public static SugarBowSetModePacket decode(FriendlyByteBuf buf) {
        int ordinal = buf.readInt();
        ShootMode mode = ShootMode.values()[ordinal];
        return new SugarBowSetModePacket(mode);
    }

    public static void handle(SugarBowSetModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack held = player.getMainHandItem();

                if (held.getItem() instanceof Sugar_Bow_Item) {
                    ShootMode newMode = ShootMode.values()[msg.modeOrdinal];
                    ShootMode.setMode(held, newMode);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}