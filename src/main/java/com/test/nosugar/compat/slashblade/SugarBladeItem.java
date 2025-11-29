package com.test.nosugar.compat.slashblade;

import com.test.nosugar.NoSugar;
import com.test.nosugar.utils.Res;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.ItemTierSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import mods.flammpfeil.slashblade.capability.slashblade.NamedBladeStateCapabilityProvider;

@SuppressWarnings("removal")
public class SugarBladeItem extends ItemSlashBlade {
    public static final ResourceLocation TEXTURE = new ResourceLocation(NoSugar.MODID, "models/named/sugar.png");
    public static final ResourceLocation MODEL = new ResourceLocation(NoSugar.MODID, "models/named/sugar.obj");

    public SugarBladeItem() {
        super(new ItemTierSlashBlade(-1, 20.0F),
                Integer.MAX_VALUE,
                1024.0F,
                new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    public static void init(ItemStack stack) {
        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            if (!stack.hasTag() || !stack.getTag().getBoolean("Initialized")) {
                state.setTexture(TEXTURE);
                state.setModel(MODEL);
                state.addSpecialEffect(Res.getResource(NoSugar.MODID, "sugar_se"));
                state.setSealed(false);
                state.setKillCount(500);
                state.setProudSoulCount(1000);
                stack.getOrCreateTag().putBoolean("Initialized", true);
                logResourcePaths(String.valueOf(state));

            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide) init(stack);
        super.inventoryTick(stack, level, entity, slot, selected);
        if(entity instanceof ServerPlayer player)logBladeState(stack, player);

    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, net.minecraft.nbt.CompoundTag nbt) {
        return new NamedBladeStateCapabilityProvider(stack);
    }

    private void logBladeState(ItemStack stack, ServerPlayer player) {
        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            NoSugar.LOGGER.info("[SugarBlade] Blade State for {}:", player.getName().getString());
            NoSugar.LOGGER.info("[SugarBlade] Current Texture: {}", state.getTexture());
            NoSugar.LOGGER.info("[SugarBlade] Current Model: {}", state.getModel());
            NoSugar.LOGGER.info("[SugarBlade] Special Effects: {}", state.getSpecialEffects());
            NoSugar.LOGGER.info("[SugarBlade] Kill Count: {}", state.getKillCount());
            NoSugar.LOGGER.info("[SugarBlade] Proud Soul Count: {}", state.getProudSoulCount());
            NoSugar.LOGGER.info("[SugarBlade] Is Sealed: {}", state.isSealed());
            NoSugar.LOGGER.info("[SugarBlade] Is Broken: {}", state.isBroken());
            NoSugar.LOGGER.info("[SugarBlade] Is Initialized: {}", stack.getTag().getBoolean("Initialized"));

        });
    }

    private static void logResourcePaths(String context) {
        NoSugar.LOGGER.info("[SugarBlade] {} - Texture Path: {}", context, TEXTURE);
        NoSugar.LOGGER.info("[SugarBlade] {} - Model Path: {}", context, MODEL);
        NoSugar.LOGGER.info("[SugarBlade] {} - Full Texture Resource: {}", context,
                String.format("assets/%s/textures/%s.png", NoSugar.MODID, TEXTURE.getPath().replace(".png", "")));
        NoSugar.LOGGER.info("[SugarBlade] {} - Full Model Resource: {}", context,
                String.format("assets/%s/models/%s", NoSugar.MODID, MODEL.getPath()));

        if (true) { //適当
            NoSugar.LOGGER.warn("[SugarBlade] DEV MODE: Check if these files exist in your resources folder:");
            NoSugar.LOGGER.warn("[SugarBlade] DEV MODE: {} -> should exist at: src/main/resources/assets/{}/textures/{}",
                    TEXTURE, NoSugar.MODID, TEXTURE.getPath());
            NoSugar.LOGGER.warn("[SugarBlade] DEV MODE: {} -> should exist at: src/main/resources/assets/{}/models/{}",
                    MODEL, NoSugar.MODID, MODEL.getPath());
        }
    }
}