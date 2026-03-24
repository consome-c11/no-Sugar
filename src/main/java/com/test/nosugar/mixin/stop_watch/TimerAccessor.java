package com.test.nosugar.mixin.stop_watch;

import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface TimerAccessor {
    @Accessor("lastMs")
    long getLastMs();
    
    @Accessor("lastMs")
    void setLastMs(long time);

    @Final @Accessor("msPerTick")
    float getmsPerTick();

}