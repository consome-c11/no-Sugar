package com.test.nosugar.shader;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public class ModShaders {
    public static ShaderInstance SIMPLE_SHADER;

    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("nosugar", "item"),
                        DefaultVertexFormat.NEW_ENTITY
                ),
                shader -> SIMPLE_SHADER = shader
        );
    }
}