package com.test.nosugar.utils.entity.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

public class ForceHaloEvent extends PlayerEvent {
    private boolean forceHalo;

    public ForceHaloEvent(Player player, boolean forceHalo) {
        super(player);
        this.forceHalo = forceHalo;
    }

    public boolean isForceHalo() {
        return forceHalo;
    }

    public void setForceHalo(boolean forceHalo) {
        this.forceHalo = forceHalo;
    }
}
