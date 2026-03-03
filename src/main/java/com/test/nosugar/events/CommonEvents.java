package com.test.nosugar.events;

import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "nosugar", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    @SubscribeEvent
    public static void onLivingMethod(LivingEntityMethodEvent event) {
        if (!(event.getEntity() instanceof LivingEntity self) || !(self instanceof ILivingEntity iliving)) return;
        if (event.getMethodType() == LivingEntityMethodEvent.MethodType.GET_HEALTH) {
            if (iliving.isErased(event.getEntity().getUUID()) || iliving.isErased()) {
                event.setReturnValue(0.f);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(player.getMaxHealth());
            }
        } else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_ALIVE) {
            if (iliving.isErased(event.getEntity().getUUID()) || iliving.isErased()) {
                event.setReturnValue(false);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(true);
            }
        } else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_DEAD_OR_DYING) {
            if (iliving.isErased(event.getEntity().getUUID()) || iliving.isErased()) {
                event.setReturnValue(true);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(false);
            }
        }
    }
}
