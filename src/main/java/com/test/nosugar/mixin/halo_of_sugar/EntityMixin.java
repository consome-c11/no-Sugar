package com.test.nosugar.mixin.halo_of_sugar;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.entity.EntityUtils;
import mods.flammpfeil.slashblade.entity.Projectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static org.joml.Math.clamp;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    private void nosugar$isPickable(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity living && EntityUtils.hasHaloOfSugar(living)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @ModifyVariable(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), argsOnly = true)
    private Vec3 nosugar$onsetDeltaMovement(Vec3 velocity) {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof Projectile projectile) {
            NoSugar.LOGGER.info(entity.toString() + "scale: " + getMultiplier(entity));
            velocity.scale(getMultiplier(entity));
        }

        return velocity;
    }

    private static float getMultiplier(Entity entity) {
        if (entity.level().isClientSide) return 1.0f;

        List<Player> players = entity.level().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(10.0));

        float closestMultiplier = 1.0f;

        for (Player player : players) {
            if (player != entity && EntityUtils.hasHaloOfSugar(player)) {
                double dist = entity.distanceTo(player);
                float multiplier = (float) clamp((dist - 1.5) / 3.5, 0.0, 1.0);

                if (multiplier < closestMultiplier) {
                    closestMultiplier = multiplier;
                }
            }
        }
        return closestMultiplier;
    }
}