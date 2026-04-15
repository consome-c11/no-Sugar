package com.test.nosugar.network;

import com.test.nosugar.network.packets.*;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;


public class ModPackets {
    private static int id = 0;

    public static void register() {
        PacketHandler.CHANNEL.registerMessage(
                id++, EraserRangeAttackPacket.class,
                EraserRangeAttackPacket::encode,
                EraserRangeAttackPacket::decode,
                EraserRangeAttackPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, WorldDestroyerChangeModePacket.class,
                WorldDestroyerChangeModePacket::encode,
                WorldDestroyerChangeModePacket::decode,
                WorldDestroyerChangeModePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, RayCastPacket.class,
                RayCastPacket::encode,
                RayCastPacket::decode,
                RayCastPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, EraseEntityPacket.class,
                EraseEntityPacket::encode,
                EraseEntityPacket::decode,
                EraseEntityPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, ChangeBagPagePacket.class,
                ChangeBagPagePacket::encode,
                ChangeBagPagePacket::decode,
                ChangeBagPagePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, SyncBagPagesPacket.class,
                SyncBagPagesPacket::encode,
                SyncBagPagesPacket::decode,
                SyncBagPagesPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, HandleErasePacket.class,
                HandleErasePacket::encode,
                HandleErasePacket::decode,
                HandleErasePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, SortBagPacket.class,
                SortBagPacket::encode,
                SortBagPacket::decode,
                SortBagPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, DestroyBlockPacket.class,
                DestroyBlockPacket::encode,
                DestroyBlockPacket::decode,
                DestroyBlockPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, SugarBowSetModePacket.class,
                SugarBowSetModePacket::encode,
                SugarBowSetModePacket::decode,
                SugarBowSetModePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        PacketHandler.CHANNEL.registerMessage(
                id++, TimeStopPacket.class,
                TimeStopPacket::encode,
                TimeStopPacket::decode,
                TimeStopPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, OpenHaloStragePacket.class,
                OpenHaloStragePacket::encode,
                OpenHaloStragePacket::decode,
                OpenHaloStragePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, SyncDeltaPacket.class,
                SyncDeltaPacket::encode,
                SyncDeltaPacket::decode,
                SyncDeltaPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, HaloFlyPacket.class,
                HaloFlyPacket::encode,
                HaloFlyPacket::decode,
                HaloFlyPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, OpenCreativeSwordMenuPacket.class,
                OpenCreativeSwordMenuPacket::encode,
                OpenCreativeSwordMenuPacket::decode,
                OpenCreativeSwordMenuPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, OpenCreativeSwordMenuResponsePacket.class,
                OpenCreativeSwordMenuResponsePacket::encode,
                OpenCreativeSwordMenuResponsePacket::decode,
                OpenCreativeSwordMenuResponsePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        PacketHandler.CHANNEL.registerMessage(
                id++, SyncCreativeSwordPacket.class,
                SyncCreativeSwordPacket::encode,
                SyncCreativeSwordPacket::decode,
                SyncCreativeSwordPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}