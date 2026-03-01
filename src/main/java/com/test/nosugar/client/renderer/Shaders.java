package com.test.nosugar.client.renderer;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.test.nosugar.NoSugar;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

//W.I.P :(
@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Shaders {
    public static ShaderInstance GLINT_COLORED;
    public static Uniform glintColorUniform;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            new ResourceLocation(NoSugar.MODID, "rendertype_glint_colored"),
                            DefaultVertexFormat.POSITION_TEX
                    ),
                    shader -> {
                        GLINT_COLORED = shader;
                        glintColorUniform = shader.getUniform("GlintColor");
                        shader.apply();
                    }
            );
        } catch (IOException | RuntimeException e) {
            NoSugar.LOGGER.debug("Failed to register glint colored shader" + e);
        }
    }
}