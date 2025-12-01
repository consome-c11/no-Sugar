package com.test.nosugar.entity.goal;


import com.test.nosugar.entity.Sand_Bag_v2;

public interface AiTask {
    boolean canUse(Sand_Bag_v2 entity, AiTaskSelector selector);

    boolean canContinue(Sand_Bag_v2 entity, AiTaskSelector selector);

    void start(Sand_Bag_v2 entity, AiTaskSelector selector);

    void stop(Sand_Bag_v2 entity, AiTaskSelector selector);

    void tick(Sand_Bag_v2 entity, AiTaskSelector selector);

    default int getPriority() {
        return Integer.MAX_VALUE;
    }
}