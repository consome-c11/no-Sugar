package com.test.nosugar.entity.goal;

import com.test.nosugar.entity.Valine3xg;

public interface AiTask {
    boolean canUse(Valine3xg entity, AiTaskSelector selector);

    boolean canContinue(Valine3xg entity, AiTaskSelector selector);

    void start(Valine3xg entity, AiTaskSelector selector);

    void stop(Valine3xg entity, AiTaskSelector selector);

    void tick(Valine3xg entity, AiTaskSelector selector);

    //小さいほど優先
    default int getPriority() {
        return Integer.MAX_VALUE;
    }
}