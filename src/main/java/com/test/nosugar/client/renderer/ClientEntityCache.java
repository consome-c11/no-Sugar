package com.test.nosugar.client.renderer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientEntityCache {
    public static final Map<Integer, ClientEntityData> entityCache = new HashMap<>();
    private static final long CLEANUP_INTERVAL = 5000; //5sec
    private static long lastCleanup = 0;

    public static ClientEntityData getOrCreateEntity(int entityId) {
        cleanupStaleEntities();

        return entityCache.computeIfAbsent(entityId, id -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return null;

            ClientEntityData data = new ClientEntityData();
            data.entity = new ClientPreviewEntity(level);
            level.addFreshEntity(data.entity);
            return data;
        });
    }

    public static void updateEntity(int entityId, double x, double y, double z,
                                    float yRot, float xRot, boolean isAttacking) {
        ClientEntityData data = getOrCreateEntity(entityId);
        if (data != null && data.entity != null) {
            data.entity.setPos(x, y, z);
            data.entity.setYRot(yRot);
            data.entity.setXRot(xRot);
            data.isAttacking = isAttacking;
            data.lastUpdate = System.currentTimeMillis();
        }
    }

    public static void removeEntity(int entityId) {
        ClientEntityData data = entityCache.remove(entityId);
        if (data != null && data.entity != null && Minecraft.getInstance().level != null) {
            data.entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static void cleanupStaleEntities() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCleanup < CLEANUP_INTERVAL) return;

        lastCleanup = currentTime;

        entityCache.entrySet().removeIf(entry -> {
            ClientEntityData data = entry.getValue();
            if (currentTime - data.lastUpdate > 10000) {
                if (data.entity != null && Minecraft.getInstance().level != null) {
                    data.entity.remove(Entity.RemovalReason.DISCARDED);
                }
                return true;
            }
            return false;
        });
    }

    public static class ClientEntityData {
        public ClientPreviewEntity entity;
        public boolean isAttacking;
        public long lastUpdate;
    }

    public static class ClientPreviewEntity extends AbstractClientPlayer {
        public static final EntityDataAccessor<Boolean> IS_ATTACKING =
                SynchedEntityData.defineId(ClientPreviewEntity.class, EntityDataSerializers.BOOLEAN);

        public ClientPreviewEntity(ClientLevel level) {
            super(level, new GameProfile(UUID.randomUUID(), "Preview"));
            this.noPhysics = true;
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
        @Override public boolean isLocalPlayer() { return false; }
        @Override public boolean isPushable() { return false; }

        @Override
        protected void defineSynchedData() {
            super.defineSynchedData();
            this.entityData.define(IS_ATTACKING, false);
        }

        public boolean isAttacking() {
            return this.entityData.get(IS_ATTACKING);
        }
    }
}