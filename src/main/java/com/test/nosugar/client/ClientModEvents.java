package com.test.nosugar.client;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModItems;
import com.test.nosugar.client.renderer.HaloRenderer;
import com.test.nosugar.client.renderer.SugarBakedModel;
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
import java.util.ArrayList;
import java.util.List;
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
        updateSugarBowModel(event, Res.getResource(NoSugar.MODID, "sugar_bow"));
        List<String> affectedItems = new ArrayList<>();

        ModItems.getAllItems().forEach(item -> {
            affectedItems.add(item.getDescriptionId());
        });

        for (String itemName : affectedItems) {
            updateModel(event, Res.getResource(NoSugar.MODID, itemName));
        }
    }

    private static void updateModel(ModelEvent.ModifyBakingResult event, ResourceLocation location) {
        ModelResourceLocation mrl = new ModelResourceLocation(location, "inventory");
        BakedModel originalModel = event.getModels().get(mrl);

        if (originalModel != null && !(originalModel instanceof SugarBakedModel)) {
            event.getModels().put(mrl, new SugarBakedModel(originalModel));
        }
    }

    private static void updateSugarBowModel(ModelEvent.ModifyBakingResult event, ResourceLocation location) {
        ModelResourceLocation mrl = new ModelResourceLocation(location, "inventory");
        BakedModel originalModel = event.getModels().get(mrl);

        if (originalModel != null && !(originalModel instanceof SugarBowBakedModel)) {
            event.getModels().put(mrl, new SugarBowBakedModel(originalModel));
        }
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
