package com.test.nosugar.entity.Utils;

import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class RandomFlyGoal extends Goal {
    private final FlyingMob mob;

    public RandomFlyGoal(FlyingMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.mob.getRandom().nextInt(2) == 0;
    }

    @Override
    public void start() {
        double x = this.mob.getX() + (this.mob.getRandom().nextDouble() - 0.5D) * 16.0D;
        double y = this.mob.getY() + (this.mob.getRandom().nextDouble() - 0.5D) * 8.0D;
        double z = this.mob.getZ() + (this.mob.getRandom().nextDouble() - 0.5D) * 16.0D;

        this.mob.getMoveControl().setWantedPosition(x, y, z, 1.0D);
    }
}
