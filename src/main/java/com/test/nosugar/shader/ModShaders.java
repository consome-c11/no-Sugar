package com.test.nosugar.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.test.nosugar.NoSugar;
import com.test.nosugar.mixin.client.RenderStateShardAccessor;
import com.test.nosugar.utils.Res;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModShaders {
    public static ShaderInstance ITEM_SHADER;
    public static Uniform time;
    public static Uniform yaw;
    public static Uniform pitch;
    public static int renderTime = 0;
    public static float renderFrame = 0.0f;
    public static RenderType ITEM_RENDER_TYPE;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), Res.getResource(NoSugar.MODID, "item"), DefaultVertexFormat.POSITION_COLOR_TEX),
                shader -> {
                    ITEM_SHADER = shader;
                    time = shader.getUniform("time");

                    ITEM_RENDER_TYPE = RenderType.create(
                            NoSugar.MODID + ":item",
                            DefaultVertexFormat.NEW_ENTITY,
                            VertexFormat.Mode.QUADS,
                            256,
                            true,
                            false,
                            RenderType.CompositeState.builder()
                                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> ITEM_SHADER))
                                    .setTransparencyState(RenderStateShardAccessor.getTranslucentTransparency())
                                    .setTextureState(RenderStateShardAccessor.getBlockSheetMipped())
                                    .setLightmapState(RenderStateShardAccessor.getLightmap())
                                    .setOverlayState(RenderStateShardAccessor.getOverlay())
                                    .createCompositeState(false)
                    );
                }
        );
        NoSugar.LOGGER.info("Reg Shader");
    }

    public static void updateTime() {
        if (time != null) {
            time.set((float) renderTime);
        }
    }

    public static void updateUniforms(float timeVal, float yawVal, float pitchVal) {
        if (time != null) time.set(timeVal);
        if (yaw != null) yaw.set(yawVal);
        if (pitch != null) pitch.set(pitchVal);
    }
}