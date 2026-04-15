package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.gui.CreativeSwordScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenCreativeSwordMenuResponsePacket {
    private final boolean isMainHand;

    public OpenCreativeSwordMenuResponsePacket(boolean isMainHand) {
        this.isMainHand = isMainHand;
    }

    public OpenCreativeSwordMenuResponsePacket(FriendlyByteBuf buffer) {
        this.isMainHand = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isMainHand);
    }

    public static OpenCreativeSwordMenuResponsePacket decode(FriendlyByteBuf buffer) {
        return new OpenCreativeSwordMenuResponsePacket(buffer);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            handleClient();
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack swordStack = this.isMainHand ? mc.player.getMainHandItem() : mc.player.getOffhandItem();
        if (swordStack.getItem() == ModItems.CREATIVE_SWORD.get()) {
            mc.setScreen(new CreativeSwordScreen(mc.player, swordStack));
        }
    }
}
