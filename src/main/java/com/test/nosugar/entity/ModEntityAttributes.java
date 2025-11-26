package com.test.nosugar.entity;

import com.test.nosugar.NoSugar;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SAND_BAG.get(), Sand_Bag.createAttributes().build());
    }
}
