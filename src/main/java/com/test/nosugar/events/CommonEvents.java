package com.test.nosugar.events;

import com.test.nosugar.NoSugar;
import com.test.nosugar.additional.SnackArmor;
import com.test.nosugar.transformer.event.AbilitiesFieldEvent;
import com.test.nosugar.transformer.event.LivingEntityFieldEvent;
import com.test.nosugar.transformer.event.LivingEntityMethodEvent;
import com.test.nosugar.utils.interfaces.ILivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NoSugar.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
            if(iliving.getDelta() > 0.f) {
                float maxHealthCap = self.getMaxHealth() - iliving.getDelta();
                event.setReturnValue(Math.min((Float) event.getReturnValue(), maxHealthCap));
            }
        } else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_ALIVE) {
            if (iliving.isErased(event.getEntity().getUUID()) || iliving.isErased()) {
                event.setReturnValue(false);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(true);
            }
            if(iliving.getDelta() > 0.f) {
                event.setReturnValue(self.getHealth() > 0.f);
            }
        } else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_DEAD_OR_DYING) {
            if (iliving.isErased(event.getEntity().getUUID()) || iliving.isErased()) {
                event.setReturnValue(true);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true)) {
                event.setReturnValue(false);
            }
            if(iliving.getDelta() > 0.f) {
                event.setReturnValue(!self.isAlive());
            }
        }
        else if (event.getMethodType() == LivingEntityMethodEvent.MethodType.IS_REMOVED) {
            if (!self.level().isClientSide && (iliving.isErased(event.getEntity().getUUID()) || iliving.isErased())) {
                event.setReturnValue(true);
                return;
            }
            if (self instanceof Player player && SnackArmor.SnackProtector.isFullSet(player, true) &&
                    self.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION &&
                    self.getRemovalReason() != Entity.RemovalReason.DISCARDED) {
                event.setReturnValue(false);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingField(LivingEntityFieldEvent event) {
        if (!(event.getEntity() instanceof LivingEntity self) || !(self instanceof Player player)) return;
        if(event.getFieldType() == LivingEntityFieldEvent.FieldType.HURT_TIME && SnackArmor.SnackProtector.isFullSet(player)) {
            event.setNewValue(0);
        }
    }
}
