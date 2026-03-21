package com.test.nosugar.client;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.ModKeyBindings;
import com.test.nosugar.client.renderer.SandBagRenderer;
import com.test.nosugar.entity.HomingArrowEntity;
import com.test.nosugar.entity.ModEntities;
import com.test.nosugar.gui.ClientBagGui;
import com.test.nosugar.gui.ModMenus;
import com.test.nosugar.utils.Res;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModRegister {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HOMING_ARROW.get(),
                context -> new ArrowRenderer<HomingArrowEntity>(context) {
                    @Override
                    public ResourceLocation getTextureLocation(HomingArrowEntity entity) {
                        return Res.getResource("minecraft", "textures/entity/projectiles/arrow.png");
                    }
                });

        event.registerEntityRenderer(ModEntities.SAND_BAG.get(), SandBagRenderer::new);

        event.registerEntityRenderer(
                ModEntities.BLOCK_SUGER_ENTITY.get(),
                context -> new ThrownItemRenderer<>(context)
        );
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeyBindings.init(event);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(ModMenus.BAG_MENU.get(), ClientBagGui::new);
    }

}
