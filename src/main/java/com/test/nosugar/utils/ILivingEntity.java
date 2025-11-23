package com.test.nosugar.utils;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface ILivingEntity {
    boolean isErased();

    void setErased(boolean erased);

    void instantKill(Player attacker, boolean SkipAnimation);

    void instantKill();

    void forceErase();

    boolean wasFullset();//SnackProtector

    void setwasFullset(boolean fullset);

    void markErased(UUID uuid);

    void unmarkErased(UUID uuid);

    boolean isErased(UUID uuid);

    // "client.ClientLevelAccessor",
    void eraseClientEntity();


}
