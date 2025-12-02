package com.test.nosugar.mixin.sugar_bow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("all")
@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor("level")
    Level getLevel();

    @Accessor("radius")
    float getRadius();

    @Accessor("x")
    double getX();

    @Accessor("y")
    double getY();

    @Accessor("z")
    double getZ();

    @Accessor("source")
    Entity getSource();

    @Final
    @Accessor("position")//?????????? なんで見つからん言われてるんやコレ
    Vec3 getPosition();
}
