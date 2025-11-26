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
    static RenderStateShard.TransparencyStateShard getTranslucentTransparency() {
        throw new AssertionError();
    }

    @Accessor("LIGHTNING_TRANSPARENCY")
    static RenderStateShard.TransparencyStateShard getLightningTransparency() {
        throw new AssertionError();
    }

    // CullStateShard
    @Accessor("NO_CULL")
    static RenderStateShard.CullStateShard getNoCull() {
        throw new AssertionError();
    }

    @Accessor("CULL")
    static RenderStateShard.CullStateShard getCull() {
        throw new AssertionError();
    }

    // DepthTestStateShard
    @Accessor("NO_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard getNoDepthTest() {
        throw new AssertionError();
    }

    @Accessor("EQUAL_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard getEqualDepthTest() {
        throw new AssertionError();
    }

    @Accessor("LEQUAL_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard getLequalDepthTest() {
        throw new AssertionError();
    }

    @Accessor("GREATER_DEPTH_TEST")
    static RenderStateShard.DepthTestStateShard getGreaterDepthTest() {
        throw new AssertionError();
    }

    // WriteMaskStateShard
    @Accessor("COLOR_WRITE")
    static RenderStateShard.WriteMaskStateShard getColorWrite() {
        throw new AssertionError();
    }

    @Accessor("DEPTH_WRITE")
    static RenderStateShard.WriteMaskStateShard getDepthWrite() {
        throw new AssertionError();
    }

    @Accessor("COLOR_DEPTH_WRITE")
    static RenderStateShard.WriteMaskStateShard getColorDepthWrite() {
        throw new AssertionError();
    }

    // LightmapStateShard
    @Accessor("NO_LIGHTMAP")
    static RenderStateShard.LightmapStateShard getNoLightmap() {
        throw new AssertionError();
    }

    @Accessor("LIGHTMAP")
    static RenderStateShard.LightmapStateShard getLightmap() {
        throw new AssertionError();
    }

    @Accessor("OVERLAY")
    static RenderStateShard.OverlayStateShard getOverlay() {
        throw new AssertionError();
    }

    // LayeringStateShard
    @Accessor("VIEW_OFFSET_Z_LAYERING")
    static RenderStateShard.LayeringStateShard getViewOffsetZLayering() {
        throw new AssertionError();
    }

    @Accessor("NO_LAYERING")
    static RenderStateShard.LayeringStateShard getNoLayering() {
        throw new AssertionError();
    }
}