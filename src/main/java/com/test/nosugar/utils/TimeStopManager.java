package com.test.nosugar.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimeStopManager {

    private static final Map<ResourceKey<Level>, Boolean> stoppedLevels = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, List<Entity>> CanMoveEntities = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, UUID> levelOwners = new ConcurrentHashMap<>();

    public static boolean isStopped(Level level) {
        return stoppedLevels.getOrDefault(level.dimension(), false);
    }

    public static boolean isOwner(Level level, Entity entity) {
        UUID ownerId = levelOwners.get(level.dimension());
        return ownerId != null && ownerId.equals(entity.getUUID());
    }

    public static void startStop(Level level, Entity owner) {
        ResourceKey<Level> dim = level.dimension();
        stoppedLevels.put(dim, true);
        levelOwners.put(dim, owner.getUUID());
        addEntity(owner); // オーナーを動けるリストに追加
    }

    public static boolean Resume(Level level, Entity entity) {
        if (!isStopped(level)) return false;

        if (isOwner(level, entity)) {
            ResourceKey<Level> dim = level.dimension();
            stoppedLevels.put(dim, false);
            levelOwners.remove(dim);
            clearEntity(level);
            return true;
        }
        return false;
    }

    public static boolean CanMove(Entity entity) {
        List<Entity> list = CanMoveEntities.get(entity.level().dimension());
        return list != null && list.contains(entity) ;
    }

    public static void addEntity(Entity entity) {
        CanMoveEntities
                .computeIfAbsent(entity.level().dimension(), k -> new ArrayList<>())
                .add(entity);
    }

    public static void removeEntity(Entity entity) {
        List<Entity> list = CanMoveEntities.get(entity.level().dimension());
        if (list != null) {
            list.remove(entity);
        }
    }

    public static void clearEntity(Level level) {
        CanMoveEntities.remove(level.dimension());
    }

    public static void clearLevel() {
        stoppedLevels.clear();
        levelOwners.clear();
    }
}