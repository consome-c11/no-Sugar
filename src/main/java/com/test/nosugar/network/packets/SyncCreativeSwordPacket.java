package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.items.CreativeSword;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCreativeSwordPacket {
    private final boolean isMainHand;
    private final int leftClickAction;
    private final int rightClickAction;
    private final boolean invulnerable;
    private final boolean aggroImmune;

    public SyncCreativeSwordPacket(boolean isMainHand, int leftClickAction, int rightClickAction, boolean invulnerable, boolean aggroImmune) {
        this.isMainHand = isMainHand;
        this.leftClickAction = leftClickAction;
        this.rightClickAction = rightClickAction;
        this.invulnerable = invulnerable;
        this.aggroImmune = aggroImmune;
    }

    public SyncCreativeSwordPacket(FriendlyByteBuf buffer) {
        this.isMainHand = buffer.readBoolean();
        this.leftClickAction = buffer.readInt();
        this.rightClickAction = buffer.readInt();
        this.invulnerable = buffer.readBoolean();
        this.aggroImmune = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isMainHand);
        buffer.writeInt(this.leftClickAction);
        buffer.writeInt(this.rightClickAction);
        buffer.writeBoolean(this.invulnerable);
        buffer.writeBoolean(this.aggroImmune);
    }

    public static SyncCreativeSwordPacket decode(FriendlyByteBuf buffer) {
        return new SyncCreativeSwordPacket(buffer);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack swordStack = this.isMainHand ? player.getMainHandItem() : player.getOffhandItem();
            if (swordStack.getItem() != ModItems.CREATIVE_SWORD.get()) return;

            if (this.leftClickAction < 0 || this.leftClickAction > 1) return;
            if (this.rightClickAction < 0 || this.rightClickAction > 1) return;

            CompoundTag tag = swordStack.getOrCreateTagElement(CreativeSword.TAG_SETTINGS);
            tag.putInt(CreativeSword.TAG_LEFT_CLICK, this.leftClickAction);
            tag.putInt(CreativeSword.TAG_RIGHT_CLICK, this.rightClickAction);
            tag.putBoolean(CreativeSword.TAG_INVULNERABLE, this.invulnerable);
            tag.putBoolean(CreativeSword.TAG_AGGRO_IMMUNE, this.aggroImmune);
        });
        context.setPacketHandled(true);
    }
}
