package com.test.nosugar.mixin.stop_watch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.test.nosugar.utils.TimeStopManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;rotLerp(FFF)F")
    )
    private float nosugar$onRotLerp(float lerp, float old, float now, Operation<Float> original, LivingEntity entity) {
        if (TimeStopManager.isStopped(entity.level()) && !TimeStopManager.CanMove(entity)) {
            return old;
        }
        return original.call(lerp, old, now);
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F")
    )
    private float nosugar$onLerp(float lerp, float old, float now, Operation<Float> original, LivingEntity entity) {
        if (TimeStopManager.isStopped(entity.level()) && !TimeStopManager.CanMove(entity)) {
            return old;
        }
        return original.call(lerp, old, now);
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;speed(F)F"),
            ordinal = 0,
            argsOnly = true)
    private float nosugar$modifyWalkSpeed(float value, LivingEntity entity) {
        if (TimeStopManager.isStopped(entity.level()) && !TimeStopManager.CanMove(entity)) {
            return 0.0F;
        }
        return value;
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;position(F)F"),
            ordinal = 1,
            argsOnly = true)
    private float nosugar$modifyWalkPosition(float value, LivingEntity entity) {
        if (TimeStopManager.isStopped(entity.level()) && !TimeStopManager.CanMove(entity)) {
            return 0.0F;
        }
        return value;
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/EntityModel;attackTime:F", opcode = 181),
            ordinal = 0,
            argsOnly = true)
    private float nosugar$modifyAttackTime(float value, LivingEntity entity) {
        if (TimeStopManager.isStopped(entity.level()) && !TimeStopManager.CanMove(entity)) {
            return 0.0F;
        }
        return value;
    }
}