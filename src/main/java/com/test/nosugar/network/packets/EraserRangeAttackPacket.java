package com.test.nosugar.network.packets;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.items.SugarSword_Item;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class EraserRangeAttackPacket {
    public EraserRangeAttackPacket() {
    }

    public static void encode(EraserRangeAttackPacket msg, FriendlyByteBuf buf) {
    }

    public static EraserRangeAttackPacket decode(FriendlyByteBuf buf) {
        return new EraserRangeAttackPacket();
    }

    public static void handle(EraserRangeAttackPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && (player.getMainHandItem().is(ModItems.SUGAR_SWORD.get()) || player.getOffhandItem().is(ModItems.SUGAR_SWORD.get()))) {
                if (SugarSword_Item.isOnCustomCooldown(player.getMainHandItem()) || SugarSword_Item.isOnCustomCooldown(player.getOffhandItem())) {
                    return;
                }
                double radius = 10.0;
                AABB area = player.getBoundingBox().inflate(radius);

                List<LivingEntity> targets = player.level().getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        e -> e != player && e.isAlive()
                );
                for (LivingEntity target : targets) {
                    if (target instanceof ILivingEntity target_) Eraser_Utils.killIfParentFound(target, player, 32);
                }
                if(player.getMainHandItem().getItem() == ModItems.SUGAR_SWORD.get())SugarSword_Item.startRangeAttackCooldown(player.getMainHandItem(), 10);
                else SugarSword_Item.startRangeAttackCooldown(player.getOffhandItem(), 10);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
