package com.test.nosugar.entity;

import com.test.nosugar.NoSugar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NoSugar.MODID);

    public static final RegistryObject<EntityType<HomingArrowEntity>> HOMING_ARROW =
            ENTITIES.register("homing_arrow",
                    () -> EntityType.Builder.<HomingArrowEntity>of(HomingArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("homing_arrow"));

    public static final RegistryObject<EntityType<Sand_Bag>> SAND_BAG =
            ENTITIES.register("sand_bag",
                    () -> EntityType.Builder.of(Sand_Bag::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build("sand_bag"));


    public static final RegistryObject<EntityType<BlockSugerEntity>> BLOCK_SUGER_ENTITY =
            ENTITIES.register("block_suger_entity", () ->
                    EntityType.Builder.<BlockSugerEntity>of(BlockSugerEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .build("block_suger_entity")
            );
}
