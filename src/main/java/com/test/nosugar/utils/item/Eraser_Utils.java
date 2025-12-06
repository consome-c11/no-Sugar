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
    public static Entity findParentEntity(Entity self) {//変えるのめんどくさかったんや許してくれ
        if (self == null || self.level() == null) return null;

        if (self instanceof PartEntity part) {
            return part.getParent();
        }
        return null;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius) {//ここらへんはEraserとかが使ってるから互換性用に残しとく
        DamageSource src = ModDamageSources.erase(self, attacker);
        if (findParentEntity(self) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false, src);
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
        if (findParentEntity(self) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false, src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player) {
            entity.instantKill(player, skipAnimation, src);
        }
        return false;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius, DamageSource src) {
        if (findParentEntity(self) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false, src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player) {
            entity.instantKill(player, false, src);
        }
        return false;
    }

    public static boolean killIfParentFound(Entity self, Entity attacker, double searchRadius, boolean skipAnimation, DamageSource src) {
        if (findParentEntity(self) instanceof ILivingEntity entity) {
            if (attacker instanceof Player player) {
                entity.instantKill(player, false, src);
                return true;
            }
        }
        if (self instanceof ILivingEntity entity && attacker instanceof Player player) {
            entity.instantKill(player, skipAnimation, src);
        }
        return false;
    }
}