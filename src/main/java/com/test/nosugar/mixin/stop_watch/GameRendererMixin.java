package com.test.nosugar.mixin.stop_watch;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private float realPartialTicks;

    /*@ModifyVariable(method = "render(FJZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float captureAndFreeze(float partialTicks) {
        this.realPartialTicks = partialTicks;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            return 0.0F;
        }
        return partialTicks;
    }*/

    /*@ModifyVariable(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V"),
            ordinal = 0,
            argsOnly = true)
    private float modifyPartialTicksForLevel(float original) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && TimeStopManager.isStopped(mc.level)) {
            return 0.0F;
        }
        return original;
    }*/

    /*@WrapOperation(
            method = "render(FJZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getDeltaFrameTime()F")
    )
    private float wrapDeltaFrameTime(Minecraft instance, Operation<Float> original) {
        float delta = original.call(instance);
        if (instance.level != null && TimeStopManager.isStopped(instance.level)) {
            return 0.0F;
        }

        return delta;
    }*/
}
