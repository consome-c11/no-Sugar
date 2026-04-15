package com.test.nosugar.utils.entity.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

public class ForceArmorEvent extends PlayerEvent {
    private boolean forceFullset;

    public ForceArmorEvent(Player player, boolean forceFullset) {
        super(player);
        this.forceFullset = forceFullset;
    }

    public boolean isForceFullset() {
        return forceFullset;
    }

    public void setForceFullset(boolean forceFullset) {
        this.forceFullset = forceFullset;
    }
}
