package com.test.nosugar.utils;

import com.test.nosugar.items.UltimaCanteen;
import com.test.nosugar.network.PacketHandler;
import com.test.nosugar.network.packets.SyncBagPagesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BagManager {
    public static void OpenBag(@NonNull Player player, UUID uuid){
        if (!player.level().isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            int totalPages = Integer.MAX_VALUE;
            int currentPage = 0;
            Level level = player.level();

            BagSavedData data = BagSavedData.get(level);
            List<ItemStack> prevPage = (currentPage > 0) ? data.getPage(uuid, currentPage - 1) : Collections.emptyList();
            List<ItemStack> currentPageItems = data.getPage(uuid, currentPage);
            List<ItemStack> nextPage = (currentPage < data.getTotalPages(uuid) - 1) ? data.getPage(uuid, currentPage + 1) : Collections.emptyList();

            PacketHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncBagPagesPacket(uuid, currentPage, prevPage, currentPageItems, nextPage)
            );

            NetworkHooks.openScreen(serverPlayer, new UltimaCanteen.BagMenuProvider(uuid, currentPage, totalPages), buf -> {
                buf.writeUUID(uuid);
                buf.writeInt(currentPage);
                buf.writeInt(totalPages);
            });
        }
    }
}
