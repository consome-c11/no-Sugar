package com.test.nosugar.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.test.nosugar.utils.render.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class FontMixin {

    @Unique
    private static final String WAVE_MARKER = ":_S";

    @Inject(method = "drawInBatch(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)I", at = @At("HEAD"), cancellable = true)
    private void nosugar$drawInBatchString(String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, boolean p_273022_, CallbackInfoReturnable<Integer> cir) {
        if (text != null && text.contains(WAVE_MARKER)) {
            String cleanText = text.replace(WAVE_MARKER, "");
            handleWavingText(cleanText, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor, p_273022_);
            cir.setReturnValue((int) 1);
            cir.cancel();
        }
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("HEAD"), cancellable = true)
    private void nosugar$drawInBatchFormatted(FormattedCharSequence sequence, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, CallbackInfoReturnable<Integer> cir) {
        if (sequence != null) {
            String text = formatSeqToString(sequence);
            if (text.contains(WAVE_MARKER)) {
                String cleanText = text.replace(WAVE_MARKER, "");
                handleWavingText(cleanText, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor, false);
                cir.setReturnValue((int) 1);
                cir.cancel();
            }
        }
    }

    @Inject(method = "drawInternal(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("HEAD"), cancellable = true)
    private void nosugar$drawInternalFormatted(FormattedCharSequence sequence, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, CallbackInfoReturnable<Integer> cir) {
        if (sequence != null) {
            String text = formatSeqToString(sequence);
            if (text.contains(WAVE_MARKER)) {
                String cleanText = text.replace(WAVE_MARKER, "");
                handleWavingText(cleanText, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor, false);
                cir.setReturnValue((int) 1);
                cir.cancel();
            }
        }
    }

    @Inject(method = "renderText(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)F", at = @At("HEAD"), cancellable = true)
    private void nosugar$renderFormatted(FormattedCharSequence sequence, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, CallbackInfoReturnable<Float> cir) {
        if (sequence != null) {
            String text = formatSeqToString(sequence);
            if (text.contains(WAVE_MARKER)) {
                String cleanText = text.replace(WAVE_MARKER, "");
                handleWavingText(cleanText, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor, false);
                cir.setReturnValue(1.0f);
                cir.cancel();
            }
        }
    }

    @Unique
    private String formatSeqToString(FormattedCharSequence sequence) {
        StringBuilder sb = new StringBuilder();
        sequence.accept((i, style, codepoint) -> {
            sb.appendCodePoint(codepoint);
            return true;
        });
        return sb.toString();
    }

    @Unique
    private void handleWavingText(String str, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, boolean forceShadow) {
        Font font = (Font) (Object) this;
        PoseStack poseStack = new PoseStack();
        poseStack.mulPoseMatrix(matrix);
        float wavespeed = Math.max(3.0f, 7.0f - (str.length() * 0.1f));
        RenderUtils.renderWavingTextDirect(
                font,
                str,
                x,
                y,
                System.nanoTime() * 0.000000001f,
                wavespeed,
                .4f,
                poseStack,
                (MultiBufferSource.BufferSource) bufferSource,
                dropShadow
        );
    }
}