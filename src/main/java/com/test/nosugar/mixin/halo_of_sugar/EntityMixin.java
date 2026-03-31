package com.test.nosugar.mixin.halo_of_sugar;

import com.test.nosugar.utils.entity.EntityUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static org.joml.Math.clamp;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    private void nosugar$isPickable(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (self instanceof LivingEntity living&& EntityUtils.hasHaloOfSugar(living)) {
            cir.setReturnValue(false);
            cir.cancel();
        }

    }

    @Inject(method = "isAttackable", at = @At("HEAD"), cancellable = true)
    private void nosugar$isAttackable(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (self instanceof LivingEntity living&& EntityUtils.hasHaloOfSugar(living)) {
            cir.setReturnValue(false);
            cir.cancel();
        }

    }

    @Inject(method = "canBeHitByProjectile", at = @At("HEAD"), cancellable = true)
    private void nosugar$canBeHitByProjectile(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (self instanceof LivingEntity living&& EntityUtils.hasHaloOfSugar(living)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
    /*@ModifyVariable(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), argsOnly = true)
    private Vec3 nosugar$onsetDeltaMovement(Vec3 velocity) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof Projectile projectile) {
            NoSugar.LOGGER.info(entity.toString() + "scale: " + getMultiplier(entity));
            velocity.scale(getMultiplier(entity));
        }

        return velocity;
    }*/

    /*@Inject(method = "baseTick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (self.isRemoved() || self instanceof Player) {
            return;
        }

        double searchRange = 6.0D;
        List<Player> nearbyPlayers = self.level().getEntitiesOfClass(
                Player.class,
                self.getBoundingBox().inflate(searchRange)
        );

        for (Player player : nearbyPlayers) {
            if(self instanceof TraceableEntity projectile && projectile.getOwner() != null && projectile.getOwner().getId() == player.getId()) continue;
            if (EntityUtils.hasHaloOfSugar(player)) {
                nosugar$applyAABB(self, player);
                break;
            }
        }
    }*/

    /*@Unique
    private void nosugar$applyAABB(Entity entity, Player player) {
        AABB entityBox = entity.getBoundingBox();
        AABB playerBox = player.getBoundingBox();
        Vec3 entityCenter = entityBox.getCenter();
        Vec3 playerCenter = playerBox.getCenter();
        Vec3 toTarget = playerCenter.subtract(entityCenter).normalize();

        double dist = nosugar$getDistanceBetweenBoxes(entityBox, playerBox);

        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-4D) {
            double dot = motion.normalize().dot(toTarget);
            if (dot <= 0) return;
        }

        double keepDistance = 1.D;
        double slowdownRange = 3.0D;

        if (dist < slowdownRange) {
            Vec3 forwardComponent = toTarget.scale(motion.dot(toTarget));
            Vec3 sideComponent = motion.subtract(forwardComponent);

            double strength = (dist - keepDistance) / (slowdownRange - keepDistance);
            strength = Math.max(0.0D, Math.pow(strength, 2));

            entity.setDeltaMovement(sideComponent.add(forwardComponent.scale(strength)));
            entity.hasImpulse = true;
        }
    }

    @Unique
    private double nosugar$getDistanceBetweenBoxes(AABB box1, AABB box2) {
        double dx = Math.max(0, Math.max(box1.minX - box2.maxX, box2.minX - box1.maxX));
        double dy = Math.max(0, Math.max(box1.minY - box2.maxY, box2.minY - box1.maxY));
        double dz = Math.max(0, Math.max(box1.minZ - box2.maxZ, box2.minZ - box1.maxZ));
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }*/
}