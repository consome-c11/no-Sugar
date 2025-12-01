package com.test.nosugar.utils;

import com.test.nosugar.additional.ModDamageSources;
import com.test.nosugar.items.ModItems;
import com.test.nosugar.mixin.sugar_bow.ExplosionAccessor;
import com.test.nosugar.utils.intercafes.IServerLevel;
import com.test.nosugar.utils.item.Eraser_Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.SimplexNoise;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SugarExplosion extends Explosion {

    private static final BlockState OVERWRITE_BLOCK_STATE = Blocks.AIR.defaultBlockState();
    private static final Random RANDOM = new Random();
    private static final double NOISE_SCALE = 0.15;
    private static final double NOISE_THRESHOLD = 0.3;

    public SugarExplosion(Level pLevel, Entity pSource, double pX, double pY, double pZ, float pRadius) {
        super(pLevel, pSource, pX, pY, pZ, pRadius, false, BlockInteraction.DESTROY);
    }

    @Override
    public void explode() {
        Level level = ((ExplosionAccessor) this).getLevel();
        float radius = ((ExplosionAccessor) this).getRadius();
        Vec3 center = ((ExplosionAccessor) this).getPosition();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        int radiusInt = (int) Math.ceil(radius);

        for (int x = -radiusInt; x <= radiusInt; x++) {
            for (int y = -radiusInt; y <= radiusInt; y++) {
                for (int z = -radiusInt; z <= radiusInt; z++) {
                    BlockPos pos = BlockPos.containing(center.x() + x, center.y() + y, center.z() + z);
                    double distanceSquared = x * x + y * y + z * z;
                    double distance = Math.sqrt(distanceSquared);

                    if (distanceSquared <= radius * radius) {
                        if (level.isInWorldBounds(pos) && level.isLoaded(pos)) {
                            double normalizedDistance = distance / radius;

                            boolean shouldRemove = true;

                            if (normalizedDistance >= 0.75) {
                                double noiseValue = getSurfaceNoise(x, y, z, radius);

                                if (normalizedDistance > 0.9) {
                                    shouldRemove = noiseValue > NOISE_THRESHOLD;
                                }
                                else {
                                    shouldRemove = noiseValue > NOISE_THRESHOLD * 1.5;
                                }
                            }

                            if (shouldRemove) {
                                if (radius < 50) { // Drop Item
                                    BlockState state = level.getBlockState(pos);
                                    ItemStack usedTool = new ItemStack(ModItems.WORLD_DESTROYER.get());

                                    LootParams.Builder builder = new LootParams.Builder((ServerLevel) level)
                                            .withParameter(LootContextParams.TOOL, usedTool)
                                            .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                                            .withParameter(LootContextParams.BLOCK_STATE, state)
                                            .withOptionalParameter(LootContextParams.THIS_ENTITY, ((ExplosionAccessor) this).getSource());

                                    List<ItemStack> drops = state.getDrops(builder);
                                    if (drops.isEmpty()) {
                                        Item item = state.getBlock().asItem();
                                        if (item != Items.AIR) {
                                            drops = List.of(new ItemStack(item));
                                        }
                                    }
                                    for (ItemStack drop : drops) {
                                        ItemEntity entity = new ItemEntity(level,
                                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                                drop);
                                        entity.setDefaultPickUpDelay();
                                        level.addFreshEntity(entity);
                                    }
                                }
                                ((IServerLevel) serverLevel).forceSetBlock(pos, OVERWRITE_BLOCK_STATE, Block.UPDATE_ALL, true);
                            }
                        }
                    }
                }
            }
        }

        List<Entity> entitiesInRadius = getEntitiesInExplosionRadius();
        for (Entity entity : entitiesInRadius) {
            Eraser_Utils.killIfParentFound(entity, ((ExplosionAccessor) this).getSource(), radiusInt, ModDamageSources.explosive(entity, ((ExplosionAccessor) this).getSource()));
        }
    }

    private double getSurfaceNoise(int x, int y, int z, float radius) {
        double noiseX = x * NOISE_SCALE + radius * 0.1;
        double noiseY = y * NOISE_SCALE + radius * 0.2;
        double noiseZ = z * NOISE_SCALE + radius * 0.3;

        double noiseValue = SimplexNoise.noise((float) noiseX, (float) noiseY, (float) noiseZ);

        return (noiseValue + 1.0) / 2.0;
    }

    private boolean shouldRemoveWithRandom(int x, int y, int z, double distance, float radius) {
        double normalizedDistance = distance / radius;

        if (normalizedDistance >= 0.8) {
            double chanceToKeep = (normalizedDistance - 0.8) * 5.0;
            return RANDOM.nextDouble() > chanceToKeep;
        }
        return true;
    }

    private List<Entity> getEntitiesInExplosionRadius() {
        Vec3 center = this.getPosition();
        float radius = ((ExplosionAccessor) this).getRadius();
        Level level = ((ExplosionAccessor) this).getLevel();
        AABB aabb = AABB.ofSize(center, radius * 2, radius * 2, radius * 2);

        List<Entity> entities = level.getEntities(null, aabb);

        return entities.stream()
                .filter(entity -> entity.distanceToSqr(center) <= radius * radius)
                .collect(Collectors.toList());
    }

    @Override
    public void finalizeExplosion(boolean spawnParticles) {
    }
}