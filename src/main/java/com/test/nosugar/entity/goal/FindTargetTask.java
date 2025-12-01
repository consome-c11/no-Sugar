package com.test.nosugar.entity.goal;


import com.test.nosugar.entity.Sand_Bag_v2;

public class FindTargetTask implements AiTask {
    @Override
    public boolean canUse(Sand_Bag_v2 entity, AiTaskSelector selector) {
        return selector.getTargetPlayer() == null &&
                selector.targetSearchCooldown <= 0;
    }

    @Override
    public boolean canContinue(Sand_Bag_v2 entity, AiTaskSelector selector) {
        return false;
    }

    @Override
    public void start(Sand_Bag_v2 entity, AiTaskSelector selector) {
        selector.findNewTarget();
    }

    @Override
    public void stop(Sand_Bag_v2 entity, AiTaskSelector selector) {
    }

    @Override
    public void tick(Sand_Bag_v2 entity, AiTaskSelector selector) {
    }

    @Override
    public int getPriority() {
        return 114514;
    }
}