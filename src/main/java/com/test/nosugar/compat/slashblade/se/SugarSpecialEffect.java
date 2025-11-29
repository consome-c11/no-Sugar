package com.test.nosugar.compat.slashblade.se;

import com.test.nosugar.compat.slashblade.SERegister;
import com.test.nosugar.items.ModItems;
import com.test.nosugar.utils.item.Eraser_Utils;
import com.test.nosugar.utils.render.ColorUtils;
import it.unimi.dsi.fastutil.chars.Char2ObjectLinkedOpenHashMap;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SugarSpecialEffect extends SpecialEffect {
    public SugarSpecialEffect() {
        super(0, true, true);
    }

    @SubscribeEvent
    public static void onUpdate(SlashBladeEvent.UpdateEvent event) {
        ISlashBladeState state = event.getSlashBladeState();

    }

    @Override
    public Component getDescription() {
        return ColorUtils.makeWaveLine("Sugar");
    }

    @SubscribeEvent
    public static void onHit(SlashBladeEvent.HitEvent event) {
        LivingEntity attacker = event.getUser();
        LivingEntity target = event.getTarget();

        /*if (attacker instanceof Player player && player.getMainHandItem().is(ModItems.SUGAR_BLADE.get())) {
            Eraser_Utils.killIfParentFound(target,attacker,16);
        }*/

    }
}
