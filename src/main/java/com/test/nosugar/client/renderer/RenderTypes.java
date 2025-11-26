package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.test.nosugar.mixin.client.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

//W.I.P :)
@SuppressWarnings("removal")
public class RenderTypes {
    public static final RenderType GLINT_COLORED_TYPE = RenderType.create(
            "glint_colored",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> Shaders.GLINT_COLORED))
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            new ResourceLocation("textures/misc/enchanted_glint_item.png"), false, false))
                    .setTransparencyState(RenderStateShardAccessor.getTranslucentTransparency())
                    .setCullState(RenderStateShardAccessor.getNoCull())
                    .setDepthTestState(RenderStateShardAccessor.getNoDepthTest())
                    .setLightmapState(RenderStateShardAccessor.getNoLightmap())
                    .setWriteMaskState(RenderStateShardAccessor.getColorWrite())
                    .setLayeringState(RenderStateShardAccessor.getViewOffsetZLayering())
                    .createCompositeState(true)
    );
}