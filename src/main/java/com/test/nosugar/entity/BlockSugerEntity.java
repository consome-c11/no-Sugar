package com.test.nosugar.entity;

import com.test.nosugar.additional.ModItems;
import com.test.nosugar.utils.SugarExplosion;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class BlockSugerEntity extends ThrowableItemProjectile {

    private static final float EXPLOSION_RADIUS = 5.0F;

    public BlockSugerEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BlockSugerEntity(Level level, LivingEntity thrower) {
        super(ModEntities.BLOCK_SUGER_ENTITY.get(), thrower, level);
    }

    public BlockSugerEntity(Level level, double x, double y, double z) {
        super(ModEntities.BLOCK_SUGER_ENTITY.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLOCK_SUGER_ITEM.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide()) {
            double x = result.getLocation().x();
            double y = result.getLocation().y();
            double z = result.getLocation().z();

            SugarExplosion explosion = new SugarExplosion(this.level(), this.getOwner(), x, y, z, EXPLOSION_RADIUS);

            explosion.explode();
            explosion.finalizeExplosion(true);

            this.discard();
        }
    }
}