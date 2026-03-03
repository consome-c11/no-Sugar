package com.test.nosugar.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;

import java.util.UUID;

public class PreviewEntity extends AbstractClientPlayer {
    public PreviewEntity(ClientLevel level) {
        super(level, new GameProfile(UUID.randomUUID(), "SandBagPreview"));
        this.noPhysics = true;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isLocalPlayer() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        //this.getEntityData().define(ClientEntityCache.ClientEntityData.IS_ATTACKING, false);
    }
}