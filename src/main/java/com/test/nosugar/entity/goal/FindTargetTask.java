package com.test.nosugar.entity.goal;

import com.test.nosugar.entity.Valine3xg;

import com.test.nosugar.entity.Valine3xg;

public class FindTargetTask implements AiTask {
    @Override
    public boolean canUse(Valine3xg entity, AiTaskSelector selector) {
        return selector.getTargetPlayer() == null &&
                selector.targetSearchCooldown <= 0;
    }

    @Override
    public boolean canContinue(Valine3xg entity, AiTaskSelector selector) {
        return false;
    }

    @Override
    public void start(Valine3xg entity, AiTaskSelector selector) {
        selector.findNewTarget();
    }

    @Override
    public void stop(Valine3xg entity, AiTaskSelector selector) {
    }

    @Override
    public void tick(Valine3xg entity, AiTaskSelector selector) {
    }

    @Override
    public int getPriority() {
        return 114514;
    }
}