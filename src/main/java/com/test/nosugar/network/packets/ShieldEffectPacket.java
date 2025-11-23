package com.test.nosugar.network.packets;

import com.test.nosugar.client.renderer.ShieldEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShieldEffectPacket {
    private final double x, y, z;

    public ShieldEffectPacket(Vec3 pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public ShieldEffectPacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static ShieldEffectPacket decode(FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new ShieldEffectPacket(x, y, z);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                ShieldEffectRenderer.spawnShield(new Vec3(x, y, z));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}