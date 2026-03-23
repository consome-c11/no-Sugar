package com.test.nosugar.mixin.stop_watch;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("timer")
    Timer getTimer();

    @Accessor("pausePartialTick")
    float getPausePartialTick();

    @Accessor("pausePartialTick")
    void setPausePartialTick(float pausePartialTick);
}
