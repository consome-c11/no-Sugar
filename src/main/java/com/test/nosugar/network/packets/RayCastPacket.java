package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.item.BlessingUtils;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RayCastPacket {
    private final List<Integer> entityIds;
    private final boolean skipdeathanim;

    public RayCastPacket(List<Integer> entityIds, boolean skipdeathanim) {
        this.skipdeathanim = skipdeathanim;
        this.entityIds = entityIds;
    }

    public RayCastPacket(List<Integer> entityIds) {
        this(entityIds, false);
    }

    public RayCastPacket(int entityId, boolean skipdeathanim) {
        this(List.of(entityId), skipdeathanim);
    }

    public RayCastPacket(int entityId) {
        this(entityId, false);
    }

    public static void encode(RayCastPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entityIds.size());
        for (int id : msg.entityIds) {
            buf.writeInt(id);
        }
        buf.writeBoolean(msg.skipdeathanim);
    }

    public static RayCastPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ids.add(buf.readInt());
        }
        boolean skip = buf.readBoolean();
        return new RayCastPacket(ids, skip);
    }

    public static void handle(RayCastPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null || sender.level().isClientSide()) return;

            Item held = sender.getMainHandItem().getItem();
            boolean isSugarSword = held == ModItems.SUGAR_SWORD.get();
            boolean isBlessedSword = BlessingUtils.hasBlessedItem(sender, BlessingUtils.ItemType.SWORD);
            boolean isCreativeSword = held == ModItems.CREATIVE_SWORD.get();

            if (!isSugarSword && !isBlessedSword && !isCreativeSword) return;

            for (int id : msg.entityIds) {
                Entity target = sender.level().getEntity(id);
                if (target != null && sender.getPosition(0).distanceTo(target.getPosition(0)) <= 4) {
                    Eraser_Utils.killIfParentFound(target, sender, 0, msg.skipdeathanim);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}