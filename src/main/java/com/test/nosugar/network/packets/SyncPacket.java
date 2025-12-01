package com.test.nosugar.network.packets;

import com.test.nosugar.NoSugar;
import com.test.nosugar.client.renderer.ClientEntityCache;
import com.test.nosugar.utils.Res;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public class SyncPacket {
    private final int entityId;
    private final double x, y, z;
    private final float yRot, xRot;
    private final boolean isAttacking;

    public SyncPacket(int entityId, double x, double y, double z,
                      float yRot, float xRot, boolean isAttacking) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
        this.isAttacking = isAttacking;
    }

    public SyncPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yRot = buf.readFloat();
        this.xRot = buf.readFloat();
        this.isAttacking = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yRot);
        buf.writeFloat(xRot);
        buf.writeBoolean(isAttacking);
    }

    public static SyncPacket decode(FriendlyByteBuf buf) {
        return new SyncPacket(buf);
    }

    @OnlyIn(Dist.CLIENT)
    public void handle(Supplier<net.minecraftforge.network.NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientEntityCache.updateEntity(
                    entityId, x, y, z, yRot, xRot, isAttacking
            );
        });
        ctx.get().setPacketHandled(true);
    }

}