package com.test.nosugar.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;

public class TimeStopManager {

    private static final Map<ResourceKey<Level>, Boolean> stoppedLevels = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, List<Entity>> CanMoveEntities = new ConcurrentHashMap<>();

    public static boolean isStopped(Level level) {
        return stoppedLevels.getOrDefault(level.dimension(), false);
    }

    public static void setStopped(Level level, boolean stopped) {
        stoppedLevels.put(level.dimension(), stopped);
    }

    public static boolean CanMove(Entity entity) {
        List<Entity> list = CanMoveEntities.get(entity.level().dimension());
        return list != null && list.contains(entity);
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
    }
}