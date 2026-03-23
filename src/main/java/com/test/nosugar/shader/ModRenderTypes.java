package com.test.nosugar.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.test.nosugar.mixin.client.RenderStateShardAccessor;
import com.test.nosugar.utils.Res;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class ModRenderTypes {
    public static final RenderType ITEM_RENDER_TYPE = RenderType.create(
            "nosugar:item",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> ModShaders.SIMPLE_SHADER))
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            Res.getResource("nosugar", "textures/misc/noise_overlay.png"),
                            false, false
                    ))
                    .setTransparencyState(RenderStateShardAccessor.getTranslucentTransparency())
                    .setCullState(RenderStateShardAccessor.getNoCull())
                    .createCompositeState(false)
    );
}
