package com.test.nosugar.additional;

/*mport com.test.eraser.NoSugar;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Supplier;

public class SchoolRegistry {
    public static final ResourceKey<net.minecraft.core.Registry<SchoolType>> SCHOOL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation(NoSugar.MODID, "schools"));

    private static final DeferredRegister<SchoolType> SCHOOLS =
            DeferredRegister.create(SCHOOL_REGISTRY_KEY, NoSugar.MODID);

    public static final Supplier<IForgeRegistry<SchoolType>> REGISTRY =
            SCHOOLS.makeRegistry(() -> new RegistryBuilder<SchoolType>().disableSaving().disableOverrides());

    public static final ResourceLocation ANCIENT_RESOURCE = new ResourceLocation(NoSugar.MODID, "ancient");

    public static final RegistryObject<SchoolType> ANCIENT;

    static {
        TagKey<Item> ancientFocusTag = TagKey.create(Registries.ITEM, new ResourceLocation(NoSugar.MODID, "ancient_focus"));

        LazyOptional<Attribute> ancientSpellPower = LazyOptional.<Attribute>empty();
        LazyOptional<Attribute> ancientMagicResist = LazyOptional.<Attribute>empty();

        LazyOptional<SoundEvent> noSound = LazyOptional.<SoundEvent>empty();

        MutableComponent displayName = Component.translatable("school.eraser.ancient").withStyle(ChatFormatting.DARK_PURPLE);

        ResourceKey<DamageType> damageTypeKey = ModDamageTypes.ERASE;

        ANCIENT = registerSchool(new SchoolType(
                ANCIENT_RESOURCE,
                ancientFocusTag,
                displayName,
                ancientSpellPower,
                ancientMagicResist,
                noSound,
                damageTypeKey
        ));
    }

    private static RegistryObject<SchoolType> registerSchool(SchoolType schoolType) {
        return SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
    }

    public static void register(IEventBus eventBus) {
        SCHOOLS.register(eventBus);
    }

    public static SchoolType getSchool(ResourceLocation resourceLocation) {
        return (SchoolType) ((IForgeRegistry) REGISTRY.get()).getValue(resourceLocation);
    }
}*/
