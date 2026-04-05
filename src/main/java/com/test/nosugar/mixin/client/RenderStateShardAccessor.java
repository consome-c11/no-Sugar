package com.test.nosugar.mixin.client;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {

    // TransparencyStateShard
    @Accessor("TRANSLUCENT_TRANSPARENCY")
    RenderStateShard.TransparencyStateShard getTranslucentTransparency();

    @Accessor("LIGHTNING_TRANSPARENCY")
    RenderStateShard.TransparencyStateShard getLightningTransparency();

    // CullStateShard
    @Accessor("NO_CULL")
    RenderStateShard.CullStateShard getNoCull();

    @Accessor("CULL")
    RenderStateShard.CullStateShard getCull();

    // DepthTestStateShard
    @Accessor("NO_DEPTH_TEST")
    RenderStateShard.DepthTestStateShard getNoDepthTest();

    @Accessor("EQUAL_DEPTH_TEST")
    RenderStateShard.DepthTestStateShard getEqualDepthTest();

    @Accessor("LEQUAL_DEPTH_TEST")
    RenderStateShard.DepthTestStateShard getLequalDepthTest();

    @Accessor("GREATER_DEPTH_TEST")
    RenderStateShard.DepthTestStateShard getGreaterDepthTest();

    // WriteMaskStateShard
    @Accessor("COLOR_WRITE")
    RenderStateShard.WriteMaskStateShard getColorWrite();

    @Accessor("DEPTH_WRITE")
    RenderStateShard.WriteMaskStateShard getDepthWrite();

    @Accessor("COLOR_DEPTH_WRITE")
    RenderStateShard.WriteMaskStateShard getColorDepthWrite();

    // LightmapStateShard
    @Accessor("NO_LIGHTMAP")
    RenderStateShard.LightmapStateShard getNoLightmap();

    @Accessor("LIGHTMAP")
    RenderStateShard.LightmapStateShard getLightmap();

    @Accessor("OVERLAY")
    RenderStateShard.OverlayStateShard getOverlay();

    // LayeringStateShard
    @Accessor("VIEW_OFFSET_Z_LAYERING")
    RenderStateShard.LayeringStateShard getViewOffsetZLayering();

    @Accessor("NO_LAYERING")
    RenderStateShard.LayeringStateShard getNoLayering();

    @Accessor("BLOCK_SHEET_MIPPED")
    RenderStateShard.TextureStateShard getBlockSheetMipped();
}