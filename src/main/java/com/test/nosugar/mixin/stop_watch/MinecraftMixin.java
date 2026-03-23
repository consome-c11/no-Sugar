package com.test.nosugar.mixin.stop_watch;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Timer;advanceTime(J)I")
    )
    private void nosugar$TickAdvance(boolean p_91384_, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (TimeStopManager.isStopped(mc.level) && TimeStopManager.CanMove(mc.player)) {
            /*Timer timer = ((MinecraftAccessor) mc).getTimer();
            TimerAccessor ITimer = (TimerAccessor) timer;
            ClientLevelAccessor ILevel = (ClientLevelAccessor) mc.level;

            long currentTime = Util.getMillis();
            long lastms = ITimer.getLastMs();
            float msPerTick = ITimer.getmsPerTick();

            ITimer.setLastMs(currentTime);

            float tick = (currentTime - lastms) / msPerTick;
            NoSugar.LOGGER.info("Time: {} LastMs: {} ret: {}", currentTime, lastms, tick);

            if (tick >= 1.0f && mc.player.getVehicle() != null) {
                //ILevel.invokeTickPassenger(mc.player.getVehicle(), mc.player);
            }
            else  {
                //mc.level.guardEntityTick(mc.level::tickNonPassenger, mc.player);
            }*/
        }
    }
}