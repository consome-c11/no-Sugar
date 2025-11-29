package com.test.nosugar.entity.goal;

import com.test.nosugar.entity.Valine3xg;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FlyToTargetTask implements AiTask {
    private static final double FLY_SPEED = 0.2;
    private static final double HOVER_HEIGHT = 2.0;
    private static final double ATTACK_RANGE = 4.0;

    @Override
    public boolean canUse(Valine3xg entity, AiTaskSelector selector) {
        Player target = selector.getTargetPlayer();
        if (target == null) return false;

        Vec3 targetPos = target.position().add(0, HOVER_HEIGHT, 0);
        return selector.distanceToSqr(entity.position(), targetPos) > ATTACK_RANGE * ATTACK_RANGE;
    }

    @Override
    public boolean canContinue(Valine3xg entity, AiTaskSelector selector) {
        Player target = selector.getTargetPlayer();
        if (target == null) return false;

        Vec3 targetPos = target.position().add(0, HOVER_HEIGHT, 0);
        return selector.distanceToSqr(entity.position(), targetPos) > ATTACK_RANGE * ATTACK_RANGE;
    }

    @Override
    public void start(Valine3xg entity, AiTaskSelector selector) {
    }

    @Override
    public void stop(Valine3xg entity, AiTaskSelector selector) {
        entity.setDeltaMovement(0, 0, 0);
    }

    @Override
    public void tick(Valine3xg entity, AiTaskSelector selector) {
        Player target = selector.getTargetPlayer();
        if (target == null) return;

        Vec3 targetPos = target.position().add(0, HOVER_HEIGHT, 0);
        Vec3 currentPos = entity.position();

        Vec3 direction = targetPos.subtract(currentPos);
        double distance = direction.length();

        if (distance < 0.1) return;

        direction = direction.normalize();

        double yDiff = (targetPos.y - currentPos.y);
        double ySpeed = yDiff * 0.1;
        ySpeed = Mth.clamp(ySpeed, -FLY_SPEED, FLY_SPEED);

        Vec3 moveVec = new Vec3(
                direction.x() * FLY_SPEED,
                ySpeed,
                direction.z() * FLY_SPEED
        );

        entity.setDeltaMovement(moveVec);
    }

    @Override
    public int getPriority() {
        return 1919;
    }
}
