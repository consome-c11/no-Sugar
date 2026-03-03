package com.test.nosugar.compat.slashblade.se;

import com.test.nosugar.utils.render.ColorUtils;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SugarSpecialEffect extends SpecialEffect {
    public SugarSpecialEffect() {
        super(0, true, true);
    }

    @SubscribeEvent
    public static void onUpdate(SlashBladeEvent.UpdateEvent event) {
        ISlashBladeState state = event.getSlashBladeState();

    }

    @SubscribeEvent
    public static void onHit(SlashBladeEvent.HitEvent event) {
        LivingEntity attacker = event.getUser();
        LivingEntity target = event.getTarget();

        /*if (attacker instanceof Player player && player.getMainHandItem().is(ModItems.SUGAR_BLADE.get())) {
            Eraser_Utils.killIfParentFound(target,attacker,16);
        }*/

    }

    @Override
    public Component getDescription() {
        return ColorUtils.makeWaveLine("Sugar");
    }
}
