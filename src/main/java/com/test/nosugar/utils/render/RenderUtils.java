package com.test.nosugar.utils.render;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.test.nosugar.NoSugar;
import com.test.nosugar.mixin.client.ClientTextTooltipAccessor;
import com.test.nosugar.mixin.client.FontAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.test.nosugar.utils.render.ColorUtils.waveGrayWhiteColor;

public class RenderUtils {

    private RenderUtils() {
    }

    private static final double WAVE_AMPLITUDE = 1.f;
    private static final double WAVE_SPEED = 3.f;
    private static final double WAVE_CHAR_SPACING = .3f;
    private static final float SHADOW_OFFSET = 1.f;
    private static final float SHADOW_DIM = 0.1f;
    private static final float SHADOW_Z_OFFSET = 0.001f;
    private static final int MAX_LIGHT = 15728880; //0xF000F0

    public static VertexConsumer getBuffer(RenderType type) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        return bufferSource.getBuffer(type);
    }

    public static void endBatch(RenderType type) {
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(type);
    }

    public static void renderBlockBox(PoseStack poseStack, Vec3 camera, BlockPos pos, int color) {
        AABB aabb = new AABB(pos);
        AABB identity = new AABB(0, 0, 0, 1, 1, 1);

        poseStack.pushPose();
        poseStack.translate(
                aabb.minX - camera.x,
                aabb.minY - camera.y,
                aabb.minZ - camera.z
        );

        LevelRenderer.renderLineBox(
                poseStack,
                getBuffer(RenderType.lines()),
                identity,
                (float) (color >> 16 & 255) / 255.0F,
                (float) (color >> 8 & 255) / 255.0F,
                (float) (color & 255) / 255.0F,
                (float) (color >> 24 & 255) / 255.0F
        );

        endBatch(RenderType.lines());
        poseStack.popPose();
    }

    public static void renderBlockList(PoseStack poseStack, Vec3 camera,
                                       List<BlockPos> positions, int color) {
        Set<BlockPos> planned = new HashSet<>(positions);

        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        float a = (float) (color >> 24 & 255) / 255.0F;

        VertexConsumer builder = getBuffer(RenderType.lines());

        final float[][] EDGES = new float[][]{
                {0, 0, 0, 1, 0, 0, 0, -1, 0},
                {1, 0, 0, 1, 0, 1, 0, -1, 0},
                {0, 0, 1, 1, 0, 1, 0, -1, 0},
                {0, 0, 0, 0, 0, 1, 0, -1, 0},
                {0, 1, 0, 1, 1, 0, 0, 1, 0},
                {1, 1, 0, 1, 1, 1, 0, 1, 0},
                {0, 1, 1, 1, 1, 1, 0, 1, 0},
                {0, 1, 0, 0, 1, 1, 0, 1, 0},
                {0, 0, 0, 0, 1, 0, -1, 0, 0},
                {0, 0, 1, 0, 1, 1, -1, 0, 0},
                {1, 0, 0, 1, 1, 0, 1, 0, 0},
                {1, 0, 1, 1, 1, 1, 1, 0, 0},
        };

        final int[][][] NEIGHBORS = new int[][][]{
                {{0, -1, 0}, {0, 0, -1}},
                {{0, -1, 0}, {1, 0, 0}},
                {{0, -1, 0}, {0, 0, 1}},
                {{0, -1, 0}, {-1, 0, 0}},
                {{0, 1, 0}, {0, 0, -1}},
                {{0, 1, 0}, {1, 0, 0}},
                {{0, 1, 0}, {0, 0, 1}},
                {{0, 1, 0}, {-1, 0, 0}},
                {{-1, 0, 0}, {0, 0, -1}},
                {{-1, 0, 0}, {0, 0, 1}},
                {{1, 0, 0}, {0, 0, -1}},
                {{1, 0, 0}, {0, 0, 1}},
        };

        for (BlockPos pos : positions) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() - camera.x,
                    pos.getY() - camera.y,
                    pos.getZ() - camera.z);

            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix = poseStack.last().normal();

            for (int i = 0; i < EDGES.length; i++) {
                float[] e = EDGES[i];
                int[][] ns = NEIGHBORS[i];

                BlockPos n1 = pos.offset(ns[0][0], ns[0][1], ns[0][2]);
                BlockPos n2 = pos.offset(ns[1][0], ns[1][1], ns[1][2]);

                if (planned.contains(n1) || planned.contains(n2)) continue;

                drawLine(builder, matrix, normalMatrix,
                        e[0], e[1], e[2],
                        e[3], e[4], e[5],
                        r, g, b, a,
                        e[6], e[7], e[8]);
            }

            poseStack.popPose();
        }

        endBatch(RenderType.lines());
    }

    private static void drawLine(VertexConsumer builder, Matrix4f matrix, Matrix3f normalMatrix,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float r, float g, float b, float a,
                                 float nx, float ny, float nz) {
        builder.vertex(matrix, x1, y1, z1)
                .color(r, g, b, a)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
        builder.vertex(matrix, x2, y2, z2)
                .color(r, g, b, a)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
    }

    public static void drawBillboardQuad(PoseStack poseStack, VertexConsumer consumer,
                                         float r, float g, float b, float a) {
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        consumer.vertex(matrix, -0.5f, -0.5f, 0).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 0, -1).endVertex();
        consumer.vertex(matrix, -0.5f, 0.5f, 0).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 0, -1).endVertex();
        consumer.vertex(matrix, 0.5f, 0.5f, 0).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 0, -1).endVertex();
        consumer.vertex(matrix, 0.5f, -0.5f, 0).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 0, -1).endVertex();
    }

    public static void renderWavingText(
            Font font,
            ClientTextTooltip tooltip,
            float x,
            float y,
            double timeSec,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffer
    ) {
        FormattedCharSequence text = ((ClientTextTooltipAccessor) tooltip).getText();
        if (text == null) return;

        FontAccessor fontAccessor = (FontAccessor) font;
        boolean filterFishy = fontAccessor.nosugar$getFilterFishyGlyphs();

        List<CharData> chars = new ArrayList<>();
        text.accept((idx, style, code) -> {
            chars.add(new CharData(code, style));
            return true;
        });

        float currentX = x;
        float baseY = y;

        for (int i = 0; i < chars.size(); i++) {
            CharData data = chars.get(i);
            Style style = data.style;

            FontSet fontSet = fontAccessor.invokeGetFontSet(style.getFont());
            GlyphInfo glyphInfo = fontSet.getGlyphInfo(data.code, filterFishy);
            BakedGlyph bakedGlyph = style.isObfuscated() && data.code != 32
                    ? fontSet.getRandomGlyph(glyphInfo)
                    : fontSet.getGlyph(data.code);

            boolean bold = style.isBold();
            boolean italic = style.isItalic();
            float boldOffset = bold ? glyphInfo.getBoldOffset() : 0.0f;
            float shadowOff = glyphInfo.getShadowOffset();

            float r, g, b;
            TextColor textColor = style.getColor();
            if (textColor != null) {
                int color = textColor.getValue();
                r = (float) (color >> 16 & 255) / 255.0f;
                g = (float) (color >> 8 & 255) / 255.0f;
                b = (float) (color & 255) / 255.0f;
            } else {
                r = 1.0f;
                g = 1.0f;
                b = 1.0f;
            }
            float a = 1.0f;

            float waveOffset = (float) (Math.sin(timeSec * WAVE_SPEED + i * WAVE_CHAR_SPACING) * WAVE_AMPLITUDE);

            if (!(bakedGlyph instanceof EmptyGlyph)) {
                Matrix4f shadowMatrix = new Matrix4f(poseStack.last().pose());
                shadowMatrix.translate(0, waveOffset, 0);

                VertexConsumer shadowConsumer = buffer.getBuffer(bakedGlyph.renderType(Font.DisplayMode.NORMAL));
                fontAccessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX + shadowOff, baseY + waveOffset + shadowOff,
                        shadowMatrix, shadowConsumer,
                        r * SHADOW_DIM, g * SHADOW_DIM, b * SHADOW_DIM, a,
                        15728880
                );
            }

            if (!(bakedGlyph instanceof EmptyGlyph)) {
                Matrix4f charMatrix = new Matrix4f(poseStack.last().pose());
                charMatrix.translate(0, waveOffset, SHADOW_Z_OFFSET);

                VertexConsumer charConsumer = buffer.getBuffer(bakedGlyph.renderType(Font.DisplayMode.NORMAL));
                fontAccessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX, baseY + waveOffset,
                        charMatrix, charConsumer,
                        r, g, b, a,
                        15728880
                );
            }

            currentX += glyphInfo.getAdvance(bold);
        }
    }

    public static float renderWavingTextRaw(
            Font font,
            String text,
            float x,
            float y,
            double timeSec,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffer,
            int color,
            boolean dropShadow,
            Font.DisplayMode displayMode,
            int packedLight
    ) {
        FontAccessor fontAccessor = (FontAccessor) font;
        boolean filterFishy = fontAccessor.nosugar$getFilterFishyGlyphs();

        float currentX = x;
        float baseY = y;

        float baseR = (float) (color >> 16 & 255) / 255.0f;
        float baseG = (float) (color >> 8 & 255) / 255.0f;
        float baseB = (float) (color & 255) / 255.0f;
        float baseA = (float) (color >> 24 & 255) / 255.0f;

        float dimFactor = dropShadow ? 0.25f : 1.0f;
        float shadowOff = dropShadow ? 1.0f : 0.0f;

        int charIndex = 0;
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            Style style = Style.EMPTY.withColor(color & 0x00FFFFFF);

            FontSet fontSet = fontAccessor.invokeGetFontSet(style.getFont());
            GlyphInfo glyphInfo = fontSet.getGlyphInfo(codePoint, filterFishy);
            BakedGlyph bakedGlyph = fontSet.getGlyph(codePoint);

            boolean bold = style.isBold();
            boolean italic = style.isItalic();
            float boldOffset = bold ? glyphInfo.getBoldOffset() : 0.0f;

            float r = baseR * dimFactor;
            float g = baseG * dimFactor;
            float b = baseB * dimFactor;
            float a = baseA;

            float waveOffset = (float) (Math.sin(timeSec * WAVE_SPEED + charIndex * WAVE_CHAR_SPACING) * WAVE_AMPLITUDE);

            if (!(bakedGlyph instanceof EmptyGlyph)) {
                Matrix4f shadowMatrix = new Matrix4f(poseStack.last().pose());
                shadowMatrix.translate(0, waveOffset, 0);

                VertexConsumer shadowConsumer = buffer.getBuffer(bakedGlyph.renderType(displayMode));
                fontAccessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX + shadowOff, baseY + waveOffset + shadowOff,
                        shadowMatrix, shadowConsumer,
                        r * SHADOW_DIM, g * SHADOW_DIM, b * SHADOW_DIM, a,
                        packedLight
                );

                Matrix4f charMatrix = new Matrix4f(poseStack.last().pose());
                charMatrix.translate(0, waveOffset, SHADOW_Z_OFFSET);

                VertexConsumer charConsumer = buffer.getBuffer(bakedGlyph.renderType(displayMode));
                fontAccessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX, baseY + waveOffset,
                        charMatrix, charConsumer,
                        r, g, b, a,
                        packedLight
                );
            }

            currentX += glyphInfo.getAdvance(bold);
            charIndex++;
            i += Character.charCount(codePoint);
        }

        return currentX;
    }

    public static float renderWavingTextRaw(
            Font font,
            String text,
            float x,
            float y,
            double timeSec,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffer,
            int color,
            boolean dropShadow,
            Font.DisplayMode displayMode,
            int packedLight,
            int backgroundColor,
            boolean forceShadow
    ) {
        FontAccessor fontAccessor = (FontAccessor) font;
        boolean filterFishy = fontAccessor.nosugar$getFilterFishyGlyphs();

        float currentX = x;
        float baseY = y;

        float baseR = (float) (color >> 16 & 255) / 255.0f;
        float baseG = (float) (color >> 8 & 255) / 255.0f;
        float baseB = (float) (color & 255) / 255.0f;
        float baseA = (float) (color >> 24 & 255) / 255.0f;

        float dimFactor = dropShadow ? 0.25f : 1.0f; // shadow dim factor
        float shadowOff = dropShadow ? 1.0f : 0.0f; // shadow offset

        int charIndex = 0;
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            Style style = Style.EMPTY.withColor(color & 0x00FFFFFF);

            FontSet fontSet = fontAccessor.invokeGetFontSet(style.getFont());
            GlyphInfo glyphInfo = fontSet.getGlyphInfo(codePoint, filterFishy);
            BakedGlyph bakedGlyph = fontSet.getGlyph(codePoint);

            boolean bold = style.isBold();
            boolean italic = style.isItalic();
            float boldOffset = bold ? glyphInfo.getBoldOffset() : 0.0f;

            float r = baseR * dimFactor;
            float g = baseG * dimFactor;
            float b = baseB * dimFactor;
            float a = baseA;

            float waveOffset = (float) (Math.sin(timeSec * WAVE_SPEED + charIndex * WAVE_CHAR_SPACING) * WAVE_AMPLITUDE);

            if (!(bakedGlyph instanceof EmptyGlyph)) {
                Matrix4f shadowMatrix = new Matrix4f(poseStack.last().pose());
                shadowMatrix.translate(0, waveOffset, 0);

                VertexConsumer shadowConsumer = buffer.getBuffer(bakedGlyph.renderType(displayMode));
                fontAccessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX + shadowOff, baseY + waveOffset + shadowOff,
                        shadowMatrix, shadowConsumer,
                        r * SHADOW_DIM, g * SHADOW_DIM, b * SHADOW_DIM, a,
                        packedLight
                );

                Matrix4f charMatrix = new Matrix4f(poseStack.last().pose());
                charMatrix.translate(0, waveOffset, SHADOW_Z_OFFSET);

                VertexConsumer charConsumer = buffer.getBuffer(bakedGlyph.renderType(displayMode));
                fontAccessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX, baseY + waveOffset,
                        charMatrix, charConsumer,
                        baseR, baseG, baseB, baseA,
                        255
                );
            }

            currentX += glyphInfo.getAdvance(bold);
            charIndex++;
            i += Character.charCount(codePoint);
        }

        return currentX;
    }

    //サンキューチャッピー
    public static float renderWavingTextDirect(
            Font font,
            String text,
            float x,
            float y,
            double timeSec,
            double waveSpeed,
            double waveAmplitude,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffer,
            boolean dropShadow
    ) {
        FontAccessor accessor = (FontAccessor) font;
        FontSet fontSet = accessor.invokeGetFontSet(Style.DEFAULT_FONT);
        boolean filterFishy = accessor.nosugar$getFilterFishyGlyphs();
        Matrix4f baseMatrix = poseStack.last().pose();

        float currentX = x;
        int charIndex = 0;

        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            char c = (char) codePoint;

            int waveColorInt = waveGrayWhiteColor((long) (timeSec * 30), charIndex, 6);
            float r = (float) ((waveColorInt >> 16) & 0xFF) / 255.0F;
            float g = (float) ((waveColorInt >> 8) & 0xFF) / 255.0F;
            float b = (float) (waveColorInt & 0xFF) / 255.0F;
            float a = 1.0F;

            float phaseOffset = charIndex * 0.2f;
            float waveOffset = (float) (Math.sin(timeSec * waveSpeed + phaseOffset) * waveAmplitude);

            GlyphInfo glyphInfo = fontSet.getGlyphInfo(c, filterFishy);
            BakedGlyph bakedGlyph = fontSet.getGlyph(c);
            boolean bold = false;
            boolean italic = false;
            float boldOffset = bold ? glyphInfo.getBoldOffset() : 0.0F;
            float shadowOff = glyphInfo.getShadowOffset();

            if (!(bakedGlyph instanceof EmptyGlyph)) {
                if (dropShadow) {
                    Matrix4f shadowMatrix = new Matrix4f(baseMatrix).translate(0, waveOffset, 0);
                    VertexConsumer shadowVC = buffer.getBuffer(bakedGlyph.renderType(Font.DisplayMode.NORMAL));
                    accessor.invokeRenderChar(
                            bakedGlyph, bold, italic, boldOffset,
                            currentX + shadowOff, y + waveOffset + shadowOff,
                            shadowMatrix, shadowVC,
                            r * SHADOW_DIM, g * SHADOW_DIM, b * SHADOW_DIM, a,
                            MAX_LIGHT
                    );
                }

                Matrix4f charMatrix = new Matrix4f(baseMatrix).translate(0, waveOffset, SHADOW_Z_OFFSET);
                VertexConsumer charVC = buffer.getBuffer(bakedGlyph.renderType(Font.DisplayMode.NORMAL));
                accessor.invokeRenderChar(
                        bakedGlyph, bold, italic, boldOffset,
                        currentX, y + waveOffset,
                        charMatrix, charVC,
                        r, g, b, a,
                        MAX_LIGHT
                );
            }

            currentX += glyphInfo.getAdvance(bold);
            charIndex++;
            i += Character.charCount(codePoint);
        }

        return currentX;
    }

    private record CharData(int code, Style style) {}
}