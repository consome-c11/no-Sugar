package com.test.nosugar.utils.item;

import com.test.nosugar.additional.ModDamageSources;
import com.test.nosugar.utils.intercafes.ILivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.PartEntity;

import java.util.List;

public class Eraser_Utils {
    public static Entity findParentEntity(Entity self, double searchRadius) {
        if (self == null || self.level() == null) return null;

        AABB box = self.getBoundingBox().inflate(searchRadius);
        List<Entity> nearby = self.level().getEntities(self, box);

        for (Entity e : nearby) {
            if (e == self) continue;

            PartEntity<?>[] parts = e.getParts();
            if (parts != null) {
                for (PartEntity<?> part : parts) {
                    if (part == self) {
                        return e;
                    }
                }
            }
        }

        return null;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius) {
        DamageSource src = ModDamageSources.erase(self, attacker);
        if (findParentEntity(self, searchRadius) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false,src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player) {
            entity.instantKill(player, false, src);
        }
        return false;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius, boolean skipAnimation) {
        DamageSource src = ModDamageSources.erase(self, attacker);
        if (findParentEntity(self, searchRadius) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false, src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player)
            entity.instantKill(player, skipAnimation,src);

        return false;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius,DamageSource src) {
        if (findParentEntity(self, searchRadius) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false,src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player) {
            entity.instantKill(player, false, src);
        }
        return false;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius, boolean skipAnimation,DamageSource src) {
        if (findParentEntity(self, searchRadius) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false, src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player)
            entity.instantKill(player, skipAnimation,src);

        return false;
    }
}

