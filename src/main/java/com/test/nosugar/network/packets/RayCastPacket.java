package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.item.BlessingUtils;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RayCastPacket {
    private final int entityId;

    public RayCastPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(RayCastPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static RayCastPacket decode(FriendlyByteBuf buf) {
        return new RayCastPacket(buf.readInt());
    }

    public static void handle(RayCastPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            Item held = sender.getMainHandItem().getItem();
            if (held != ModItems.SUGAR_SWORD.get() && held != ModItems.WORLD_DESTROYER.get() &&
                    !BlessingUtils.hasBlessedItem(sender,BlessingUtils.ItemType.SWORD)) return;
            if (sender != null) {
                Entity target = sender.level().getEntity(msg.entityId);
                if (sender.level().isClientSide()) return;
                if (target != null && sender.getPosition(0).distanceTo(target.getPosition(0)) <= 4) {
                    Eraser_Utils.killIfParentFound(target, sender, 32);

                    //System.out.println("RayCastPacket: processed entity ID " + msg.entityId);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
