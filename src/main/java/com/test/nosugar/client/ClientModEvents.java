package com.test.nosugar.client;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.client.renderer.HaloRenderer;
import com.test.nosugar.client.renderer.SugarBowBakedModel;
import com.test.nosugar.utils.Res;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.io.IOException;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    /*@SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    if (tintIndex == 1) {
                        long time = System.currentTimeMillis();
                        double wave = (Math.sin(time / 700.0) + 1.0) / 2.0;
                        int gray = 0xCCCCCC;
                        int white = 0xFFFFFF;
                        int r = (int) (((gray >> 16) & 0xFF) * (1 - wave) + ((white >> 16) & 0xFF) * wave);
                        int g = (int) (((gray >> 8) & 0xFF) * (1 - wave) + ((white >> 8) & 0xFF) * wave);
                        int b = (int) ((gray & 0xFF) * (1 - wave) + (white & 0xFF) * wave);
                        return (r << 16) | (g << 8) | b;
                    }
                    return 0xFFFFFF;
                }, ModItems.SUGAR_SWORD.get(),
                ModItems.WORLD_DESTROYER.get());

    }*/

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();

        ModelResourceLocation pulling0Location = new ModelResourceLocation(
                Res.getResource(NoSugar.MODID, "sugar_bow_pulling_0"), "inventory");
        BakedModel pulling0Model = modelRegistry.get(pulling0Location);

        if (pulling0Model == null) {
            NoSugar.LOGGER.debug("Warning: pulling_0 model not found in assets");
        }

        ModelResourceLocation pulling1Location = new ModelResourceLocation(
                Res.getResource(NoSugar.MODID, "sugar_bow_pulling_1"), "inventory");

        BakedModel pulling1Model = modelRegistry.get(pulling1Location);

        if (pulling1Model == null) {
            NoSugar.LOGGER.debug("Warning: pulling_1 model not found in assets");
        }

        ModelResourceLocation pulling2Location = new ModelResourceLocation(
                Res.getResource(NoSugar.MODID, "sugar_bow_pulling_2"), "inventory");
        BakedModel pulling2Model = modelRegistry.get(pulling2Location);

        if (pulling2Model == null) {
            NoSugar.LOGGER.debug("Warning: pulling_2 model not found in assets");
        }


        ModelResourceLocation originalModelLocation = new ModelResourceLocation(
                Res.getResource(NoSugar.MODID, "sugar_bow"), "inventory");

        BakedModel originalModel = modelRegistry.get(originalModelLocation);

        if (originalModel != null) {
            BakedModel customModel = new SugarBowBakedModel(originalModel);

            modelRegistry.put(originalModelLocation, customModel);

            NoSugar.LOGGER.debug("Successfully replaced model for: " + originalModelLocation);
        } else {
            NoSugar.LOGGER.debug("Original model not found for: " + originalModelLocation);
        }
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        //ModShaders.register(event);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(
                    ModItems.HALO_OF_SUGAR.get(),
                    HaloRenderer::new
            );
        });
    }

}
